package com.inclufin.backend.app.loan.domain.model

data class LoanPaymentPlansResponse(
    val basePaymentPlan: PaymentPlan,
    val termReductionPlan: PaymentPlan? = null,
    val rateReductionPlan: PaymentPlan? = null
)
