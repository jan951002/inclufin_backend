package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

data class PaymentPlan(
    val totalAmountPaid: BigDecimal,
    val totalInterestPaid: BigDecimal,
    val installments: List<Installment>,
    val planType: PaymentPlanType,
    val reductionDetails: ReductionDetails? = null
)
