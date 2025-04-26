package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

data class Rate(
    val percentage: BigDecimal,
    val type: InterestRateType
)
