package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CalculateLoanPaymentRequestDto(
    @field:NotNull(message = "loan_amount cannot be null")
    @field:DecimalMin(value = "0.01", message = "loan_amount must be positive")
    @JsonProperty("loan_amount")
    val loanAmount: BigDecimal,

    @field:NotNull(message = "term_in_months cannot be null")
    @field:Min(value = 2, message = "term_in_months be at least 2 months")
    @JsonProperty("term_in_months")
    val termInMonths: Int,

    @field:NotNull(message = "interest_rate_percentage cannot be null")
    @field:DecimalMin(value = "0.01", message = "interest_rate_percentage must be positive")
    @JsonProperty("interest_rate_percentage")
    val interestRatePercentage: BigDecimal,

    @field:NotNull(message = "interest_rate_type cannot be null")
    @JsonProperty("interest_rate_type")
    val interestRateType: ApiInterestRateType,

    @field:Valid
    @JsonProperty("capital_contribution") val capitalContribution: CapitalContributionRequestDto? = null,
)