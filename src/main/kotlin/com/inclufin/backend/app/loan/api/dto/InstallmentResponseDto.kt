package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class InstallmentResponseDto(
    @JsonProperty("installment_number") val installmentNumber: Int,
    @JsonProperty("initial_balance") val initialBalance: BigDecimal,
    @JsonProperty("interest_paid") val interestPaid: BigDecimal,
    @JsonProperty("principal_paid") val principalPaid: BigDecimal,
    @JsonProperty("total_payment") val totalPayment: BigDecimal,
    @JsonProperty("ending_balance") val endingBalance: BigDecimal
)
