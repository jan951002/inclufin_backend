package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class ApiInterestRateType {
    @JsonProperty("effective_annual")
    EFFECTIVE_ANNUAL,

    @JsonProperty("nominal_monthly_due")
    NOMINAL_MONTHLY_DUE
}
