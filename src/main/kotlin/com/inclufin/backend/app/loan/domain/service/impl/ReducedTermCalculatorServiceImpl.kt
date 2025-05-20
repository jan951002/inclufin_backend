package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_CALCULATION
import com.inclufin.backend.app.loan.domain.exception.MissingCapitalContributionException
import com.inclufin.backend.app.loan.domain.ext.roundToDisplayScale
import com.inclufin.backend.app.loan.domain.model.CapitalRecoveryFactorParams
import com.inclufin.backend.app.loan.domain.model.Installment
import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.model.PaymentPlan
import com.inclufin.backend.app.loan.domain.model.PaymentPlanType
import com.inclufin.backend.app.loan.domain.model.Rate
import com.inclufin.backend.app.loan.domain.model.ReductionDetails.TermReduction
import com.inclufin.backend.app.loan.domain.service.CapitalRecoveryFactorCalculator
import com.inclufin.backend.app.loan.domain.service.InterestSavedCalculator
import com.inclufin.backend.app.loan.domain.service.PeriodicRateCalculator
import com.inclufin.backend.app.loan.domain.service.ReducedTermPaymentCalculator
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ReducedTermCalculatorServiceImpl(
    private val capitalRecoveryFactorCalculator: CapitalRecoveryFactorCalculator,
    private val periodicRateCalculator: PeriodicRateCalculator,
    private val interestSavedCalculator: InterestSavedCalculator
) : ReducedTermPaymentCalculator {

    override fun calculatePaymentPlan(loanRequest: LoanRequest): PaymentPlan = with(loanRequest) {
        val capitalContribution = capitalContribution
            ?: throw MissingCapitalContributionException(
                "Capital contribution information is required for reduced term calculations."
            )

        val periodicInterestRate = getPeriodicInterestRate(interestRate)
        
        val crfParams = CapitalRecoveryFactorParams(
            periodicRate = periodicInterestRate,
            termInMonths = termInMonths,
            amount = loanAmount
        )
        
        val initialInstallmentAmountPrecise = capitalRecoveryFactorCalculator.calculatePayment(crfParams)

        var currentBalance = loanAmount
        val installments = mutableListOf<Installment>()
        var totalInterestPaid = BigDecimal.ZERO
        var monthsPaid = 0

        for (i in 1..termInMonths) {
            val interestPaidPrecise = calculateInterestPaid(currentBalance, periodicInterestRate)
            val principalPaidWithoutContributionPrecise =
                subtract(initialInstallmentAmountPrecise, interestPaidPrecise)
            var additionalPrincipalPaidPrecise = BigDecimal.ZERO
            var totalPaymentForMonthPrecise = initialInstallmentAmountPrecise

            if (i >= capitalContribution.startMonth && currentBalance > BigDecimal.ZERO) {
                additionalPrincipalPaidPrecise = capitalContribution.contributionAmount.min(currentBalance)
                totalPaymentForMonthPrecise = add(initialInstallmentAmountPrecise, capitalContribution.contributionAmount)
                    .min(add(currentBalance, interestPaidPrecise))
            }

            val totalPrincipalPaidPrecise = add(principalPaidWithoutContributionPrecise, additionalPrincipalPaidPrecise)
                .min(currentBalance)

            val endingBalancePrecise = subtract(currentBalance, totalPrincipalPaidPrecise)

            installments.add(
                Installment(
                    installmentNumber = i,
                    initialBalance = currentBalance.roundToDisplayScale(),
                    interestPaid = interestPaidPrecise.roundToDisplayScale(),
                    principalPaid = totalPrincipalPaidPrecise.roundToDisplayScale(),
                    totalPayment = totalPaymentForMonthPrecise.roundToDisplayScale(),
                    endingBalance = endingBalancePrecise.roundToDisplayScale()
                )
            )
            totalInterestPaid = add(totalInterestPaid, interestPaidPrecise)
            currentBalance = endingBalancePrecise
            monthsPaid++

            if (currentBalance <= BigDecimal.ZERO) break
        }

        val totalAmountPaid = installments.sumOf { it.totalPayment }
        val interestSaved = calculateInterestSaved(this, totalInterestPaid)
        val monthsSaved = termInMonths - monthsPaid

        return PaymentPlan(
            totalAmountPaid = totalAmountPaid.roundToDisplayScale(),
            totalInterestPaid = totalInterestPaid.roundToDisplayScale(),
            installments = installments,
            planType = PaymentPlanType.REDUCED_TERM,
            reductionDetails = TermReduction(
                interestSaved = interestSaved.roundToDisplayScale(),
                monthsSaved = monthsSaved
            )
        )
    }

    private fun getPeriodicInterestRate(
        rate: Rate
    ) = periodicRateCalculator.calculateDecimalPeriodicRate(rate)

    private fun calculateInterestSaved(
        loanRequest: LoanRequest,
        totalInterestPaid: BigDecimal
    ) = interestSavedCalculator.calculate(loanRequest, totalInterestPaid)
    
    private fun calculateInterestPaid(
        currentBalance: BigDecimal, 
        periodicInterestRate: BigDecimal
    ): BigDecimal = 
        currentBalance.multiply(periodicInterestRate, MC_CALCULATION)
    
    private fun add(a: BigDecimal, b: BigDecimal): BigDecimal = 
        a.add(b, MC_CALCULATION)
    
    private fun subtract(a: BigDecimal, b: BigDecimal): BigDecimal = 
        a.subtract(b, MC_CALCULATION)
}
