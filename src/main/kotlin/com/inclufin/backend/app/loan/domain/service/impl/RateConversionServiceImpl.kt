package com.inclufin.backend.app.loan.domain.service.impl

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.DEFAULT_ROUNDING_MODE
import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.MC_CALCULATION
import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.ONE_HUNDRED
import com.inclufin.backend.app.loan.domain.model.InterestRateType
import com.inclufin.backend.app.loan.domain.model.Rate
import com.inclufin.backend.app.loan.domain.service.RateConverter
import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.math.pow

@Service
class RateConversionServiceImpl : RateConverter {

    /**
     * Converts a given interest rate to its equivalent nominal monthly due rate.
     *
     * If the input rate is already `NOMINAL_MONTHLY_DUE`, it is returned unchanged.
     * If the input rate is `EFFECTIVE_ANNUAL`, it applies the formula:
     * `Monthly Rate = [(1 + Annual Rate / 100)^(1/12) - 1] * 100`
     *
     * Note: Since `BigDecimal` does not natively support fractional exponents,
     * a temporary conversion to `Double` is performed for the power calculation (1/12th root).
     * This may introduce minimal precision loss. A higher internal calculation precision
     * (`CALCULATION_PRECISION`) is used to mitigate this before final rounding.
     *
     * @param rate The original interest rate (either effective annual or nominal monthly).
     * @return The equivalent rate expressed as a nominal monthly due rate.
     */
    override fun convertToMonthlyRate(rate: Rate): Rate {
        return when (rate.type) {
            InterestRateType.EFFECTIVE_ANNUAL -> {
                val annualRateDecimal = rate.percentage.divide(ONE_HUNDRED, MC_CALCULATION)
                val annualFactor = BigDecimal.ONE + annualRateDecimal
                val monthlyFactorDouble = annualFactor.toDouble().pow(MONTHLY_EXPONENT)
                val monthlyFactor = BigDecimal(monthlyFactorDouble.toString())
                val monthlyRateDecimal = monthlyFactor - BigDecimal.ONE
                val monthlyRateValue = monthlyRateDecimal.multiply(ONE_HUNDRED)
                    .setScale(FINAL_SCALE, DEFAULT_ROUNDING_MODE)

                Rate(
                    monthlyRateValue,
                    InterestRateType.NOMINAL_MONTHLY_DUE
                )
            }

            InterestRateType.NOMINAL_MONTHLY_DUE -> rate
        }
    }

    /**
     * Converts a given interest rate to its equivalent
     * effective annual rate (EAR).
     *
     * If the input rate is already `EFFECTIVE_ANNUAL`, it is returned unchanged.
     * If the input rate is `NOMINAL_MONTHLY_DUE`, it applies the formula:
     * `EAR = [(1 + Monthly Rate / 100)^12 - 1] * 100`
     *
     * This calculation uses `BigDecimal.pow(Int)` for the exponentiation.
     *
     * @param rate The original interest rate (either effective annual or nominal monthly).
     * @return The equivalent rate expressed as an effective annual rate.
     */
    override fun convertToAnnualRate(rate: Rate): Rate {
        return when (rate.type) {
            InterestRateType.NOMINAL_MONTHLY_DUE -> {
                val monthlyRateDecimal = rate.percentage.divide(ONE_HUNDRED, MC_CALCULATION)
                val monthlyFactor = BigDecimal.ONE + monthlyRateDecimal
                val annualFactor = monthlyFactor.pow(MONTHS_IN_YEAR, MC_CALCULATION)
                val annualRateDecimal = annualFactor - BigDecimal.ONE
                val annualRateValue = annualRateDecimal.multiply(ONE_HUNDRED)

                Rate(
                    annualRateValue,
                    InterestRateType.EFFECTIVE_ANNUAL
                )
            }

            InterestRateType.EFFECTIVE_ANNUAL -> rate
        }
    }

    companion object {
        private const val MONTHS_IN_YEAR = 12
        private const val MONTHLY_EXPONENT = 1.0 / MONTHS_IN_YEAR
        private const val FINAL_SCALE = 10
    }
}
