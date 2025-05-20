package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

/**
 * Model class that encapsulates all parameters needed for capital recovery factor calculations.
 *
 * @property periodicRate The periodic interest rate (as a decimal, e.g., 0.01 for 1%).
 * @property termInMonths The total number of periods (months).
 * @property amount The amount to be multiplied by the CRF (e.g., loan amount or current balance).
 */
data class CapitalRecoveryFactorParams(
    val periodicRate: BigDecimal,
    val termInMonths: Int,
    val amount: BigDecimal
)
