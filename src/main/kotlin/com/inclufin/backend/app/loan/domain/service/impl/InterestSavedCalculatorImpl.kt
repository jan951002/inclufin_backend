package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_CALCULATION
import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.service.CapitalRecoveryFactorCalculator
import com.inclufin.backend.app.loan.domain.service.InterestSavedCalculator
import com.inclufin.backend.app.loan.domain.service.PeriodicRateCalculator
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class InterestSavedCalculatorImpl(
    private val periodicRateCalculator: PeriodicRateCalculator,
    private val capitalRecoveryFactorCalculator: CapitalRecoveryFactorCalculator
) : InterestSavedCalculator {

    override fun calculate(originalLoanRequest: LoanRequest, totalInterestPaid: BigDecimal): BigDecimal {
        val periodicRate = periodicRateCalculator.calculateDecimalPeriodicRate(originalLoanRequest.interestRate)

        val capitalRecoveryFactor = capitalRecoveryFactorCalculator.calculate(
            periodicRate = periodicRate,
            termInMonths = originalLoanRequest.termInMonths
        )

        val originalTotalInterest: BigDecimal = if (periodicRate.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO
        } else {
            val originalTotalPaid = originalLoanRequest.loanAmount
                .multiply(capitalRecoveryFactor, MC_CALCULATION)
                .multiply(BigDecimal(originalLoanRequest.termInMonths), MC_CALCULATION)
            originalTotalPaid.subtract(originalLoanRequest.loanAmount, MC_CALCULATION)
        }

        return originalTotalInterest
            .subtract(totalInterestPaid)
            .max(BigDecimal.ZERO)
    }
}