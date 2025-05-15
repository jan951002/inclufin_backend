package com.inclufin.backend.app.loan.domain.service

import com.inclufin.backend.app.loan.domain.model.Rate

/**
 * Defines the contract for services that convert interest rates between different types.
 * Implementations of this interface are responsible for providing the logic
 * to transform a rate from one representation (e.g., annual) to another (e.g., monthly)
 * and vice versa, ensuring the financial equivalence is maintained according to
 * standard financial formulas.
 */
interface RateConverter {

    /**
     * Specifies the requirement to convert a given interest rate into its
     * equivalent nominal monthly due rate.
     *
     * The specific conversion logic depends on the type of the input `rate`.
     *
     * @param rate The original interest rate to be converted.
     * @return The equivalent rate expressed as a nominal monthly due rate.
     */
    fun convertToMonthlyRate(rate: Rate): Rate

    /**
     * Specifies the requirement to convert a given interest rate into its
     * equivalent effective annual rate (EAR).
     *
     * The specific conversion logic depends on the type of the input `rate`.
     *
     * @param rate The original interest rate to be converted.
     * @return The equivalent rate expressed as an effective annual rate.
     */
    fun convertToAnnualRate(rate: Rate): Rate
}
