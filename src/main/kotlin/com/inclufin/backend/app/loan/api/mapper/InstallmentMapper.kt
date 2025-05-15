package com.inclufin.backend.app.loan.api.mapper

import com.inclufin.backend.app.loan.api.dto.InstallmentResponseDto
import com.inclufin.backend.app.loan.domain.model.Installment

interface InstallmentMapper {

    fun toDto(installment: Installment): InstallmentResponseDto
}
