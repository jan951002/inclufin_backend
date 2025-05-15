package com.inclufin.backend.app.loan.api.mapper

import com.inclufin.backend.app.loan.api.dto.PaymentPlanResponseDto
import com.inclufin.backend.app.loan.domain.model.PaymentPlan

interface PaymentPlanMapper {

    fun toDto(domain: PaymentPlan): PaymentPlanResponseDto
}
