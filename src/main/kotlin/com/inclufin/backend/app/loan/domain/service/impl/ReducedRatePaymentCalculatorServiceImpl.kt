package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_CALCULATION
import com.inclufin.backend.app.loan.domain.exception.MissingCapitalContributionException
import com.inclufin.backend.app.loan.domain.ext.roundToDisplayScale
import com.inclufin.backend.app.loan.domain.model.Installment
import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.model.PaymentPlan
import com.inclufin.backend.app.loan.domain.model.PaymentPlanType
import com.inclufin.backend.app.loan.domain.model.Rate
import com.inclufin.backend.app.loan.domain.model.ReductionDetails
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

        val periodicInterestRate = periodicRateCalculator.calculateDecimalPeriodicRate(interestRate)
        val totalInstallments = termInMonths
        val startMonthOfCapitalContribution = capitalContribution.startMonth
        val monthlyCapitalContribution = capitalContribution.contributionAmount
        var currentBalance = loanAmount

        // Helper para calcular la cuota francesa: cuota = saldo * CRF(periodicRate, plazo)
        fun calculateInstallmentAmount(balance: BigDecimal, term: Int): BigDecimal {
            val crf = capitalRecoveryFactorCalculator.calculate(periodicInterestRate, term)
            return balance.multiply(crf, MC_CALCULATION)
        }

        val installments = mutableListOf<Installment>()
        val cuotaOriginal = calculateInstallmentAmount(loanAmount, totalInstallments)

        for (i in 0 until totalInstallments) {
            val installmentNumber = i + 1
            val initialBalance = currentBalance
            val interestPaid = currentBalance.multiply(periodicInterestRate, MC_CALCULATION)
            val isContributionMonth = installmentNumber == startMonthOfCapitalContribution
            val plazoRestante = totalInstallments - i

            val currentInstallmentAmount = when {
                installmentNumber == 1 -> cuotaOriginal
                else -> calculateInstallmentAmount(currentBalance, plazoRestante)
            }

            val principalPaid = when {
                isContributionMonth -> {
                    val abono = cuotaOriginal.subtract(interestPaid, MC_CALCULATION).add(monthlyCapitalContribution)
                    if (abono > currentBalance) currentBalance else abono
                }
                else -> {
                    val abono = currentInstallmentAmount.subtract(interestPaid, MC_CALCULATION)
                    if (abono > currentBalance) currentBalance else abono
                }
            }

            val totalPayment = when {
                isContributionMonth -> cuotaOriginal.add(monthlyCapitalContribution)
                else -> currentInstallmentAmount
            }

            currentBalance = currentBalance.subtract(principalPaid, MC_CALCULATION)

            installments.add(
                Installment(
                    installmentNumber = installmentNumber,
                    initialBalance = initialBalance.roundToDisplayScale(),
                    interestPaid = interestPaid.roundToDisplayScale(),
                    principalPaid = principalPaid.roundToDisplayScale(),
                    totalPayment = totalPayment.roundToDisplayScale(),
                    endingBalance = currentBalance.roundToDisplayScale()
                )
            )
            if (currentBalance <= BigDecimal.ZERO) break
        }

        val lastInstallmentAmountPrecise = installments.lastOrNull()?.totalPayment ?: BigDecimal.ZERO
        val totalAmountPaidPrecise = installments.sumOf { it.totalPayment }
        val totalInterestPaidPrecise = installments.sumOf { it.interestPaid }
        val interestSaved = calculateInterestSaved(this, totalInterestPaidPrecise)

        PaymentPlan(
            totalAmountPaid = totalAmountPaidPrecise.roundToDisplayScale(),
            totalInterestPaid = totalInterestPaidPrecise.roundToDisplayScale(),
            installments = installments,
            planType = PaymentPlanType.REDUCED_RATE,
            reductionDetails = ReductionDetails.RateReduction(
                interestSaved = interestSaved.roundToDisplayScale(),
                finalPayment = lastInstallmentAmountPrecise.roundToDisplayScale()
            )
        )
    }

    private fun calculateInterestSaved(
        loanRequest: LoanRequest,
        totalInterestPaid: BigDecimal
    ) = interestSavedCalculator.calculate(loanRequest, totalInterestPaid)

}
