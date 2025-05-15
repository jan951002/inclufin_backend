package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_CALCULATION
import com.inclufin.backend.app.loan.domain.ext.roundToDisplayScale
import com.inclufin.backend.app.loan.domain.model.Installment
import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.model.PaymentPlan
import com.inclufin.backend.app.loan.domain.model.PaymentPlanType
import com.inclufin.backend.app.loan.domain.model.Rate
import com.inclufin.backend.app.loan.domain.service.CapitalRecoveryFactorCalculator
import com.inclufin.backend.app.loan.domain.service.TraditionalPaymentCalculator
import com.inclufin.backend.app.loan.domain.service.PeriodicRateCalculator
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class TraditionalCalculatorServiceImpl(
    private val capitalRecoveryFactorCalculator: CapitalRecoveryFactorCalculator,
    private val periodicRateCalculator: PeriodicRateCalculator,
) : TraditionalPaymentCalculator {

    override fun calculatePaymentPlan(loanRequest: LoanRequest): PaymentPlan {
        val periodicInterestRate = getPeriodicInterestRate(loanRequest.interestRate)

        val capitalRecoveryFactor = calculateCapitalRecoveryFactor(
            periodicRate = periodicInterestRate,
            termInMonths = loanRequest.termInMonths
        )
        val installmentAmountRounded = loanRequest.loanAmount
            .multiply(capitalRecoveryFactor, MC_CALCULATION)
            .roundToDisplayScale()

        var currentBalance = loanRequest.loanAmount
        val installments = mutableListOf<Installment>()
        var totalInterestPaid = BigDecimal.ZERO

        for (i in 1..loanRequest.termInMonths) {

            val interestPaidPrecise = currentBalance.multiply(periodicInterestRate, MC_CALCULATION)
            val principalPaidPrecise = installmentAmountRounded.subtract(interestPaidPrecise, MC_CALCULATION)
            val endingBalancePrecise = currentBalance.subtract(principalPaidPrecise, MC_CALCULATION)

            totalInterestPaid = totalInterestPaid.add(interestPaidPrecise, MC_CALCULATION)

            installments.add(
                Installment(
                    installmentNumber = i,
                    initialBalance = currentBalance.roundToDisplayScale(),
                    interestPaid = interestPaidPrecise.roundToDisplayScale(),
                    principalPaid = principalPaidPrecise.roundToDisplayScale(),
                    totalPayment = installmentAmountRounded.roundToDisplayScale(),
                    endingBalance = endingBalancePrecise.roundToDisplayScale()
                )
            )
            currentBalance = endingBalancePrecise
        }

        val totalAmountPaidPrecise = loanRequest.loanAmount.add(totalInterestPaid, MC_CALCULATION)

        return PaymentPlan(
            totalAmountPaid = totalAmountPaidPrecise.roundToDisplayScale(),
            totalInterestPaid = totalInterestPaid.roundToDisplayScale(),
            installments = installments,
            planType = PaymentPlanType.TRADITIONAL
        )
    }

    private fun getPeriodicInterestRate(
        rate: Rate
    ) = periodicRateCalculator.calculateDecimalPeriodicRate(rate)

    private fun calculateCapitalRecoveryFactor(
        periodicRate: BigDecimal,
        termInMonths: Int
    ) = capitalRecoveryFactorCalculator.calculate(periodicRate, termInMonths)
}
