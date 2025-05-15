package com.inclufin.backend.app.loan.api.mapper.impl

import com.inclufin.backend.app.loan.api.dto.CalculateLoanPaymentRequestDto
import com.inclufin.backend.app.loan.api.mapper.CalculateLoanRequestMapper
import com.inclufin.backend.app.loan.api.mapper.CapitalContributionTypeMapper
import com.inclufin.backend.app.loan.api.mapper.RateMapper
import com.inclufin.backend.app.loan.domain.model.CapitalContribution
import com.inclufin.backend.app.loan.domain.model.LoanRequest
import org.springframework.stereotype.Component

@Component
class CalculateLoanRequestMapperImpl(
    private val capitalContributionTypeMapper: CapitalContributionTypeMapper,
    private val rateMapper: RateMapper
) : CalculateLoanRequestMapper {

    override fun toDomain(dto: CalculateLoanPaymentRequestDto) = LoanRequest(
        loanAmount = dto.loanAmount,
        termInMonths = dto.termInMonths,
        interestRate = rateMapper.toDomain(dto.interestRatePercentage, dto.interestRateType),
        capitalContribution = dto.capitalContribution?.let {
            CapitalContribution(
                startMonth = it.startMonth,
                contributionAmount = it.contributionAmount,
                types = capitalContributionTypeMapper.toDomain(it.types)
            )
        }
    )
}
