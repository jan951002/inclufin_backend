package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class ReductionDetailsResponseDto(
    @JsonProperty("interest_saved") val interestSaved: BigDecimal,
    @JsonProperty("type") val type: ApiCapitalContributionType,
    @JsonProperty("months_saved") val monthsSaved: Int?,
    @JsonProperty("final_payment") val finalPayment: BigDecimal?
)
