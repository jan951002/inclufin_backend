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
import com.inclufin.backend.app.loan.domain.service.ReducedTermPaymentCalculator
import com.inclufin.backend.app.loan.domain.service.InterestSavedCalculator
import com.inclufin.backend.app.loan.domain.service.PeriodicRateCalculator
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
            val interestPaidPrecise = currentBalance.multiply(periodicInterestRate, MC_CALCULATION)
            val principalPaidWithoutContributionPrecise =
                initialInstallmentAmountPrecise.subtract(interestPaidPrecise, MC_CALCULATION)
            var additionalPrincipalPaidPrecise = BigDecimal.ZERO
            var totalPaymentForMonthPrecise = initialInstallmentAmountPrecise

            if (i >= capitalContribution.startMonth && currentBalance > BigDecimal.ZERO) {
                additionalPrincipalPaidPrecise = capitalContribution.contributionAmount.min(currentBalance)
                totalPaymentForMonthPrecise = initialInstallmentAmountPrecise
                    .add(capitalContribution.contributionAmount, MC_CALCULATION)
                    .min(currentBalance.add(interestPaidPrecise, MC_CALCULATION))
            }

            val totalPrincipalPaidPrecise = principalPaidWithoutContributionPrecise
                .add(additionalPrincipalPaidPrecise, MC_CALCULATION)
                .min(currentBalance)

            val endingBalancePrecise = currentBalance.subtract(totalPrincipalPaidPrecise, MC_CALCULATION)

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
            totalInterestPaid = totalInterestPaid.add(interestPaidPrecise, MC_CALCULATION)
            currentBalance = endingBalancePrecise
            monthsPaid++

            if (currentBalance <= BigDecimal.ZERO) break
        }

        val totalAmountPaidPrecise = installments.sumOf { it.totalPayment }
        val totalInterestPaidPrecise = installments.sumOf { it.interestPaid }
        val interestSaved = calculateInterestSaved(this, totalInterestPaidPrecise)
        val monthsSaved = termInMonths - monthsPaid

        return PaymentPlan(
            totalAmountPaid = totalAmountPaidPrecise.roundToDisplayScale(),
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
}
