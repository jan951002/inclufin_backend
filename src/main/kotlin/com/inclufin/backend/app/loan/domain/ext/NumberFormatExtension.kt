package com.inclufin.backend.app.loan.domain.ext

import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.DEFAULT_ROUNDING_MODE
import com.inclufin.backend.app.loan.domain.config.LoanCalculationConfig.DISPLAY_SCALE
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Rounds the BigDecimal value to the standard display scale (typically 2 decimal places for currency)
 * using the default rounding mode (HALF_UP).
 *
 * @return A new BigDecimal instance rounded to the display scale.
 */
fun BigDecimal.roundToDisplayScale(): BigDecimal = this.setScale(DISPLAY_SCALE, DEFAULT_ROUNDING_MODE)