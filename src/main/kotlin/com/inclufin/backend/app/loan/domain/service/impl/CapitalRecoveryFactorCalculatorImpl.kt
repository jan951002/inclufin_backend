package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_CALCULATION
import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_DIVISION
import com.inclufin.backend.app.loan.domain.service.CapitalRecoveryFactorCalculator
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CapitalRecoveryFactorCalculatorImpl : CapitalRecoveryFactorCalculator {

    override fun calculate(periodicRate: BigDecimal, termInMonths: Int): BigDecimal {
        if (periodicRate.compareTo(BigDecimal.ZERO) == 0) {
            return if (termInMonths == 0) {
                BigDecimal.ZERO
            } else {
                BigDecimal.ONE.divide(BigDecimal(termInMonths), MC_DIVISION)
            }
        }

        if (termInMonths == 0) {
            return BigDecimal.ZERO
        }

        val onePlusRate = BigDecimal.ONE.add(periodicRate, MC_CALCULATION)
        val onePlusRatePowN = onePlusRate.pow(termInMonths, MC_CALCULATION)
        val numerator = periodicRate.multiply(onePlusRatePowN, MC_CALCULATION)
        val denominator = onePlusRatePowN.subtract(BigDecimal.ONE, MC_CALCULATION)

        return if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal.ZERO
        } else {
            numerator.divide(denominator, MC_DIVISION)
        }
    }
}
