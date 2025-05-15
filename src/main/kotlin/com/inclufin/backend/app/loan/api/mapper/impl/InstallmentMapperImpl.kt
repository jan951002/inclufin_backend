package com.inclufin.backend.app.loan.api.mapper.impl

import com.inclufin.backend.app.loan.api.dto.InstallmentResponseDto
import com.inclufin.backend.app.loan.api.mapper.InstallmentMapper
import com.inclufin.backend.app.loan.domain.model.Installment
import org.springframework.stereotype.Component

@Component
class InstallmentMapperImpl : InstallmentMapper {

    override fun toDto(installment: Installment): InstallmentResponseDto = InstallmentResponseDto(
        installmentNumber = installment.installmentNumber,
        initialBalance = installment.initialBalance,
        interestPaid = installment.interestPaid,
        principalPaid = installment.principalPaid,
        totalPayment = installment.totalPayment,
        endingBalance = installment.endingBalance
    )
}
