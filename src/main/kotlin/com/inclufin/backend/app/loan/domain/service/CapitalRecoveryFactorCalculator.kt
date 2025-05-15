package com.inclufin.backend.app.loan.domain.service

import java.math.BigDecimal

/**
 * Defines the contract for calculating the Capital Recovery Factor (CRF).
 * The CRF is used to determine the fixed payment amount for an annuity (like a loan).
 */
interface CapitalRecoveryFactorCalculator {

    /**
     * Calculates the Capital Recovery Factor.
     * Formula: CRF = [i * (1 + i)^n] / [(1 + i)^n - 1]
     * where 'i' is the periodic interest rate and 'n' is the number of periods.
     *
     * Handles edge cases like zero interest rate or zero term.
     *
     * @param periodicRate The periodic interest rate (as a decimal, e.g., 0.01 for 1%).
     * @param termInMonths The total number of periods (months).
     * @return The calculated Capital Recovery Factor as a BigDecimal.
     */
    fun calculate(periodicRate: BigDecimal, termInMonths: Int): BigDecimal
}
