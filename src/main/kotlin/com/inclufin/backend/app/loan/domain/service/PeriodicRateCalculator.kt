package com.inclufin.backend.app.loan.domain.service

import com.inclufin.backend.app.loan.domain.model.Rate
import java.math.BigDecimal

/**
 * Defines the contract for calculating the periodic (e.g., monthly) interest rate
 * as a decimal value from a Rate object.
 */
interface PeriodicRateCalculator {

    /**
     * Calculates the periodic interest rate as a decimal.
     * This typically involves converting the input rate to the correct period (e.g., monthly)
     * and then dividing the percentage by 100.
     *
     * @param rate The input Rate object (could be annual, monthly, etc.).
     * @return The periodic interest rate as a BigDecimal decimal (e.g., 0.01 for 1%).
     */
    fun calculateDecimalPeriodicRate(rate: Rate): BigDecimal
}
