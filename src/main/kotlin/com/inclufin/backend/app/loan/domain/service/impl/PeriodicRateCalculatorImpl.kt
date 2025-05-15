package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_DIVISION
import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.ONE_HUNDRED
import com.inclufin.backend.app.loan.domain.model.Rate
import com.inclufin.backend.app.loan.domain.service.PeriodicRateCalculator
import com.inclufin.backend.app.loan.domain.service.RateConverter
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@Service
class PeriodicRateCalculatorImpl(
    private val rateConverter: RateConverter
) : PeriodicRateCalculator {

    override fun calculateDecimalPeriodicRate(rate: Rate): BigDecimal {
        val monthlyRate = rateConverter.convertToMonthlyRate(rate)
        return monthlyRate.percentage.divide(ONE_HUNDRED, MC_DIVISION)
    }
}
