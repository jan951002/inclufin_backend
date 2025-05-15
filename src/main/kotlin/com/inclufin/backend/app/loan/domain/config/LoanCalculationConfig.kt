package com.inclufin.backend.app.loan.domain.config

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Central configuration object for constants and settings used in loan calculations.
 * Provides consistent precision and rounding modes across different calculation services.
 */
object LoanCalculationConfig {

    /**
     * The precision (number of significant digits) to use for intermediate BigDecimal calculations.
     * A higher precision minimizes rounding errors during complex calculations.
     */
    const val CALCULATION_PRECISION = 20

    /**
     * The default rounding mode used for intermediate calculations and final display rounding.
     * HALF_UP is the standard rounding method (round up if the discarded fraction is >= 0.5).
     */
    val DEFAULT_ROUNDING_MODE: RoundingMode = RoundingMode.HALF_UP

    /**
     * MathContext for general high-precision calculations.
     */
    val MC_CALCULATION = MathContext(CALCULATION_PRECISION, DEFAULT_ROUNDING_MODE)

    /**
     * MathContext specifically recommended for division operations to ensure sufficient precision.
     * Often the same as MC_CALCULATION, but defined separately for clarity or future specialization.
     */
    val MC_DIVISION = MathContext(CALCULATION_PRECISION, DEFAULT_ROUNDING_MODE)

    /**
     * Constant for the value 100 as BigDecimal, commonly used for percentage conversions.
     */
    val ONE_HUNDRED: BigDecimal = BigDecimal(100)

    /**
     * Constant for the value 1 as BigDecimal.
     */
    val ONE: BigDecimal = BigDecimal.ONE

    /**
     * The standard scale (number of decimal places) for displaying monetary values.
     * Typically 2 for currencies with cents/centavos.
     */
    const val DISPLAY_SCALE = 2
}