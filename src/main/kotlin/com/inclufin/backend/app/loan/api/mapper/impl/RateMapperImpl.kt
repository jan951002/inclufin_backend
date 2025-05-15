package com.inclufin.backend.app.loan.api.mapper.impl

import com.inclufin.backend.app.loan.api.dto.ApiInterestRateType
import com.inclufin.backend.app.loan.api.mapper.RateMapper
import com.inclufin.backend.app.loan.domain.model.InterestRateType
import com.inclufin.backend.app.loan.domain.model.Rate
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class RateMapperImpl : RateMapper {

    override fun toDomain(
        percentage: BigDecimal,
        type: ApiInterestRateType
    ): Rate {
        val domainType = when (type) {
            ApiInterestRateType.EFFECTIVE_ANNUAL -> InterestRateType.EFFECTIVE_ANNUAL
            ApiInterestRateType.NOMINAL_MONTHLY_DUE -> InterestRateType.NOMINAL_MONTHLY_DUE
        }
        return Rate(percentage, domainType)
    }
}
