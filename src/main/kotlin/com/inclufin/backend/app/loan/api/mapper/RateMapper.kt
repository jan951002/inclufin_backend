package com.inclufin.backend.app.loan.api.mapper

import com.inclufin.backend.app.loan.api.dto.ApiInterestRateType
import com.inclufin.backend.app.loan.domain.model.Rate
import java.math.BigDecimal

interface RateMapper {

    fun toDomain(percentage: BigDecimal, type: ApiInterestRateType): Rate
}
