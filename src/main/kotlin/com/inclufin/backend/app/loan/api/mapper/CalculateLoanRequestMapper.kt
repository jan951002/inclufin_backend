package com.inclufin.backend.app.loan.api.mapper

import com.inclufin.backend.app.loan.api.dto.CalculateLoanPaymentRequestDto
import com.inclufin.backend.app.loan.domain.model.LoanRequest

interface CalculateLoanRequestMapper {

    fun toDomain(dto: CalculateLoanPaymentRequestDto): LoanRequest
}
