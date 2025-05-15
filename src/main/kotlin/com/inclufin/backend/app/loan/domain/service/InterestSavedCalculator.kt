package com.inclufin.backend.app.loan.domain.service

import com.inclufin.backend.app.loan.domain.model.LoanRequest
import java.math.BigDecimal

/**
 * Defines the contract for calculating the amount of interest saved
 * compared to an original loan plan, typically due to prepayments or contributions.
 */
interface InterestSavedCalculator {

    /**
     * Calculates the difference between the total interest that would have been paid
     * on the original loan terms and the actual total interest paid in a modified plan.
     *
     * @param originalLoanRequest The loan request representing the original
     * loan terms (e.g., without capital contributions).
     * @param totalInterestPaid The total interest actually paid in the
     * calculated (modified) payment plan.
     * @return The calculated interest saved as a BigDecimal, ensuring it's not negative.
     */
    fun calculate(originalLoanRequest: LoanRequest, totalInterestPaid: BigDecimal): BigDecimal
}
