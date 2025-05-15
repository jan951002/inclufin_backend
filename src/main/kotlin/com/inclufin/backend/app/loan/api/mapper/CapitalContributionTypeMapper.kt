package com.inclufin.backend.app.loan.api.mapper

import com.inclufin.backend.app.loan.api.dto.ApiCapitalContributionType
import com.inclufin.backend.app.loan.domain.model.CapitalContributionType

interface CapitalContributionTypeMapper {

    fun toDomain(types: List<ApiCapitalContributionType>?): List<CapitalContributionType>
}