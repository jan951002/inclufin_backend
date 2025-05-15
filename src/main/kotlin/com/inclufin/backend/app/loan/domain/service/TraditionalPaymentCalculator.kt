package com.inclufin.backend.app.loan.domain.service

import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.model.PaymentPlan

interface TraditionalPaymentCalculator {

    fun calculatePaymentPlan(loanRequest: LoanRequest): PaymentPlan
}
