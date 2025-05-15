package com.inclufin.backend.app.loan.api.mapper

import com.inclufin.backend.app.loan.api.dto.ReductionDetailsResponseDto
import com.inclufin.backend.app.loan.domain.model.ReductionDetails

interface ReductionDetailMapper {

    fun toDto(details: ReductionDetails): ReductionDetailsResponseDto
}
