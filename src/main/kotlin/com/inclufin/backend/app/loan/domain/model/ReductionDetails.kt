package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

sealed class ReductionDetails {
    data class TermReduction(
        val interestSaved: BigDecimal,
        val monthsSaved: Int
    ) : ReductionDetails()

    data class RateReduction(
        val interestSaved: BigDecimal,
        val finalPayment: BigDecimal
    ) : ReductionDetails()
}
