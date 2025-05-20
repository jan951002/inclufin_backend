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


    override fun calculatePaymentPlan(loanRequest: LoanRequest): PaymentPlan {
        val capitalContribution = loanRequest.capitalContribution
            ?: throw MissingCapitalContributionException(
                "Capital contribution information is required for reduced term calculations."
            )
        val periodicInterestRate = getPeriodicInterestRate(loanRequest.interestRate)
        
        val initialCrfParams = CapitalRecoveryFactorParams(
            periodicRate = periodicInterestRate,
            termInMonths = loanRequest.termInMonths,
            amount = loanRequest.loanAmount
        )
        
        val initialInstallmentAmountPrecise = capitalRecoveryFactorCalculator.calculatePayment(initialCrfParams)

        val installments = mutableListOf<Installment>()
        var currentBalance = loanRequest.loanAmount
        var totalInterestPaid = BigDecimal.ZERO
        var monthsPaid = 0

        for (i in 0 until loanRequest.termInMonths) {
            val interestPaidPrecise = currentBalance.multiply(periodicInterestRate, MC_CALCULATION)
            val totalPaymentForMonthPrecise = if (i + 1 >= capitalContribution.startMonth) {
                val remainingCrfParams = CapitalRecoveryFactorParams(
                    periodicRate = periodicInterestRate,
                    termInMonths = loanRequest.termInMonths - i,
                    amount = currentBalance
                )
                capitalRecoveryFactorCalculator.calculatePayment(remainingCrfParams)
            } else {
                initialInstallmentAmountPrecise
            }
            var additionalToPay = BigDecimal.ZERO

            val additionalPrincipalPaidPrecise = if (i + 1 >= capitalContribution.startMonth) {
                if (currentBalance > totalPaymentForMonthPrecise) {
                    additionalToPay = capitalContribution.contributionAmount
                    totalPaymentForMonthPrecise - interestPaidPrecise + additionalToPay
                } else {
                    currentBalance
                }
            } else {
                totalPaymentForMonthPrecise - interestPaidPrecise
            }

            val endingBalancePrecise = currentBalance - additionalPrincipalPaidPrecise

            installments.add(
                Installment(
                    installmentNumber = i + 1,
                    initialBalance = currentBalance.roundToDisplayScale(),
                    interestPaid = interestPaidPrecise.roundToDisplayScale(),
                    principalPaid = additionalPrincipalPaidPrecise.roundToDisplayScale(),
                    totalPayment = (totalPaymentForMonthPrecise + additionalToPay).roundToDisplayScale(),
                    endingBalance = endingBalancePrecise.roundToDisplayScale()
                )
            )

            totalInterestPaid += interestPaidPrecise
            currentBalance = endingBalancePrecise
            monthsPaid++

            if (currentBalance <= BigDecimal.ZERO) break
        }

        val totalAmountPaidPrecise = installments.sumOf { it.totalPayment }
        val totalInterestPaidPrecise = installments.sumOf { it.interestPaid }
        val interestSaved = calculateInterestSaved(loanRequest, totalInterestPaidPrecise)
        val monthsSaved = loanRequest.termInMonths - monthsPaid

        return PaymentPlan(
            totalAmountPaid = totalAmountPaidPrecise.roundToDisplayScale(),
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
