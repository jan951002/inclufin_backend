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
import com.inclufin.backend.app.loan.domain.service.ReducedRatePaymentCalculator
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ReducedRatePaymentCalculatorServiceImpl(
    private val capitalRecoveryFactorCalculator: CapitalRecoveryFactorCalculator,
    private val periodicRateCalculator: PeriodicRateCalculator,
    private val interestSavedCalculator: InterestSavedCalculator
) : ReducedRatePaymentCalculator {

    override fun calculatePaymentPlan(loanRequest: LoanRequest): PaymentPlan = with(loanRequest) {
        val capitalContribution = capitalContribution
            ?: throw MissingCapitalContributionException(
                "Capital contribution information is required for reduced rate calculations."
            )
        val periodicInterestRate = getPeriodicInterestRate(interestRate)
        
        // Calculate the traditional payment amount
        val traditionalCrfParams = CapitalRecoveryFactorParams(
            periodicRate = periodicInterestRate,
            termInMonths = termInMonths,
            amount = loanAmount
        )
        
        val traditionalPaymentAmount = capitalRecoveryFactorCalculator.calculatePayment(traditionalCrfParams)
        
        // Calculate the reduced payment amount with capital contribution
        val reducedAmount = loanAmount.subtract(capitalContribution.contributionAmount, MC_CALCULATION)
        val reducedCrfParams = CapitalRecoveryFactorParams(
            periodicRate = periodicInterestRate,
            termInMonths = termInMonths,
            amount = reducedAmount
        )
        
        val reducedPaymentAmount = capitalRecoveryFactorCalculator.calculatePayment(reducedCrfParams)
        
        // Generate payment plan
        val installments = mutableListOf<Installment>()
        var currentBalance = loanAmount
        var totalInterestPaid = BigDecimal.ZERO
        var monthsPaid = 0

        // Mantener la inicialización de i como 0 para preservar la lógica original
        for (i in 0 until termInMonths) {
            val interestPaidPrecise = currentBalance.multiply(periodicInterestRate, MC_CALCULATION)
            val totalPaymentForMonthPrecise = if (i + 1 >= capitalContribution.startMonth) {
                val remainingCrfParams = CapitalRecoveryFactorParams(
                    periodicRate = periodicInterestRate,
                    termInMonths = termInMonths - i,
                    amount = currentBalance
                )
                capitalRecoveryFactorCalculator.calculatePayment(remainingCrfParams)
            } else {
                traditionalPaymentAmount
            }
            var additionalToPay = BigDecimal.ZERO

            val additionalPrincipalPaidPrecise = if (i + 1 >= capitalContribution.startMonth) {
                if (currentBalance > totalPaymentForMonthPrecise) {
                    additionalToPay = capitalContribution.contributionAmount
                    totalPaymentForMonthPrecise.subtract(interestPaidPrecise, MC_CALCULATION).add(additionalToPay, MC_CALCULATION)
                } else {
                    currentBalance
                }
            } else {
                totalPaymentForMonthPrecise.subtract(interestPaidPrecise, MC_CALCULATION)
            }

            val endingBalancePrecise = currentBalance.subtract(additionalPrincipalPaidPrecise, MC_CALCULATION)

            installments.add(
                Installment(
                    installmentNumber = i + 1,
                    initialBalance = currentBalance.roundToDisplayScale(),
                    interestPaid = interestPaidPrecise.roundToDisplayScale(),
                    principalPaid = additionalPrincipalPaidPrecise.roundToDisplayScale(),
                    totalPayment = totalPaymentForMonthPrecise.add(additionalToPay, MC_CALCULATION).roundToDisplayScale(),
                    endingBalance = endingBalancePrecise.roundToDisplayScale()
                )
            )

            totalInterestPaid = totalInterestPaid.add(interestPaidPrecise, MC_CALCULATION)
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
            planType = PaymentPlanType.REDUCED_RATE,
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
