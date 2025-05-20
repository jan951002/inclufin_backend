package com.inclufin.backend.app.loan.domain.service

import com.inclufin.backend.app.loan.domain.model.CapitalRecoveryFactorParams
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
    
    /**
     * Calculates the Capital Recovery Factor using the parameters from CapitalRecoveryFactorParams.
     * Formula: CRF = [i * (1 + i)^n] / [(1 + i)^n - 1]
     * where 'i' is the periodic interest rate and 'n' is the number of periods.
     *
     * @param params The parameters for the CRF calculation.
     * @return The calculated Capital Recovery Factor as a BigDecimal.
     */
    fun calculate(params: CapitalRecoveryFactorParams): BigDecimal {
        return calculate(params.periodicRate, params.termInMonths)
    }

    /**
     * Calculates the payment amount by multiplying the Capital Recovery Factor by the amount.
     * Formula: Payment = Amount * CRF
     * where CRF = [i * (1 + i)^n] / [(1 + i)^n - 1]
     *
     * Handles edge cases like zero interest rate or zero term.
     *
     * @param periodicRate The periodic interest rate (as a decimal, e.g., 0.01 for 1%).
     * @param termInMonths The total number of periods (months).
     * @param amount The amount to be multiplied by the CRF (e.g., loan amount).
     * @return The calculated payment amount as a BigDecimal.
     */
    fun calculatePayment(periodicRate: BigDecimal, termInMonths: Int, amount: BigDecimal): BigDecimal
    
    /**
     * Calculates the payment amount using the parameters from CapitalRecoveryFactorParams.
     * Formula: Payment = Amount * CRF
     * where CRF = [i * (1 + i)^n] / [(1 + i)^n - 1]
     *
     * @param params The parameters for the payment calculation.
     * @return The calculated payment amount as a BigDecimal.
     */
    fun calculatePayment(params: CapitalRecoveryFactorParams): BigDecimal {
        return calculatePayment(params.periodicRate, params.termInMonths, params.amount)
    }
}
