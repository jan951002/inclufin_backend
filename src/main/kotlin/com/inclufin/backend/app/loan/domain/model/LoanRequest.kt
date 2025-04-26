package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

data class LoanRequest(
    val loanAmount: BigDecimal,
    val termInMonths: Int,
    val interestRate: Rate,
    val capitalContribution: CapitalContribution? = null
)
