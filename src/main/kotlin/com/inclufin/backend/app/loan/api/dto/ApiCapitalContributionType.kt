package com.inclufin.backend.app.loan.api.dto

import com.fasterxml.jackson.annotation.JsonProperty

enum class ApiCapitalContributionType {
    @JsonProperty("reduced_term")
    REDUCED_TERM,

    @JsonProperty("reduced_rate")
    REDUCED_RATE,

    @JsonProperty("all")
    ALL
}
