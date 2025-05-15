package com.inclufin.backend.app.loan.api.mapper.impl

import com.inclufin.backend.app.loan.api.dto.ApiCapitalContributionType
import com.inclufin.backend.app.loan.api.mapper.CapitalContributionTypeMapper
import com.inclufin.backend.app.loan.domain.model.CapitalContributionType
import org.springframework.stereotype.Component

@Component
class CapitalContributionTypeMapperImpl : CapitalContributionTypeMapper {

    override fun toDomain(types: List<ApiCapitalContributionType>?) = types?.map {
        CapitalContributionType.valueOf(it.name)
    } ?: listOf()
}
