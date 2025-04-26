package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

data class Installment(
    val installmentNumber: Int,
    val initialBalance: BigDecimal,
    val interestPaid: BigDecimal,
    val principalPaid: BigDecimal,
    val totalPayment: BigDecimal,
    val endingBalance: BigDecimal
)
