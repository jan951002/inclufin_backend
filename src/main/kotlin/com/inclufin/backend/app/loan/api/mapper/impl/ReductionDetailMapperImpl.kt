package com.inclufin.backend.app.loan.api.mapper.impl

import com.inclufin.backend.app.loan.api.dto.ApiCapitalContributionType
import com.inclufin.backend.app.loan.api.dto.ReductionDetailsResponseDto
import com.inclufin.backend.app.loan.api.mapper.ReductionDetailMapper
import com.inclufin.backend.app.loan.domain.model.ReductionDetails
import org.springframework.stereotype.Component

@Component
class ReductionDetailMapperImpl : ReductionDetailMapper {
    override fun toDto(details: ReductionDetails) = when (details) {
        is ReductionDetails.TermReduction -> ReductionDetailsResponseDto(
            interestSaved = details.interestSaved,
            monthsSaved = details.monthsSaved,
            finalPayment = null,
            type = ApiCapitalContributionType.REDUCED_TERM
        )

        is ReductionDetails.RateReduction -> ReductionDetailsResponseDto(
            interestSaved = details.interestSaved,
            monthsSaved = null,
            finalPayment = details.finalPayment,
            type = ApiCapitalContributionType.REDUCED_RATE
        )
    }
}
