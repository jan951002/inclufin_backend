package com.inclufin.backend.app.loan.api.mapper.impl

import com.inclufin.backend.app.loan.api.dto.PaymentPlanResponseDto
import com.inclufin.backend.app.loan.api.mapper.InstallmentMapper
import com.inclufin.backend.app.loan.api.mapper.PaymentPlanMapper
import com.inclufin.backend.app.loan.api.mapper.ReductionDetailMapper
import com.inclufin.backend.app.loan.domain.model.PaymentPlan
import org.springframework.stereotype.Component

@Component
class PaymentPlanMapperImpl(
    private val reductionDetailMapper: ReductionDetailMapper,
    private val installmentMapper: InstallmentMapper,
) : PaymentPlanMapper {

    override fun toDto(domain: PaymentPlan): PaymentPlanResponseDto = PaymentPlanResponseDto(
        totalAmountPaid = domain.totalAmountPaid,
        totalInterestPaid = domain.totalInterestPaid,
        installments = domain.installments.map(installmentMapper::toDto),
        planType = domain.planType.name,
        reductionDetails = domain.reductionDetails?.let(reductionDetailMapper::toDto)
    )
}
