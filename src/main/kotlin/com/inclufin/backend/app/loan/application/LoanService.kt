package com.inclufin.backend.app.loan.application

import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.model.PaymentPlan

interface LoanService {
    fun calculateTraditionalPaymentPlan(loanRequest: LoanRequest): PaymentPlan
    fun calculatePaymentPlanWithCapitalContribution(loanRequest: LoanRequest): List<PaymentPlan>
    fun calculateAllPaymentScenarios(loanRequest: LoanRequest): List<PaymentPlan>
}
