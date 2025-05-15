package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CapitalContributionRequestDto(
    @field:NotNull(message = "start_month cannot be null")
    @field:Min(value = 1, message = "start_month must be at least 1")
    @JsonProperty("start_month")
    val startMonth: Int,

    @field:NotNull(message = "contribution_amount cannot be null")
    @field:DecimalMin(value = "1.0", message = "contribution_amount must be positive")
    @JsonProperty("contribution_amount")
    val contributionAmount: BigDecimal,

    @JsonProperty("types") val types: List<ApiCapitalContributionType> = listOf(ApiCapitalContributionType.ALL)
)
