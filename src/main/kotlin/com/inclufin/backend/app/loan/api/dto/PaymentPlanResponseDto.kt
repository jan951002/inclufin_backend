package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.inclufin.backend.app.loan.api.dto.ReductionDetailsResponseDto
import java.math.BigDecimal

data class PaymentPlanResponseDto(
    @JsonProperty("total_amount_paid") val totalAmountPaid: BigDecimal,
    @JsonProperty("total_interest_paid") val totalInterestPaid: BigDecimal,
    @JsonProperty("installments") val installments: List<InstallmentResponseDto>,
    @JsonProperty("plan_type") val planType: String,
    @JsonProperty("reduction_details") val reductionDetails: ReductionDetailsResponseDto? = null
)
