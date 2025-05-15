package com.inclufin.backend.app.loan.application.impl

import com.inclufin.backend.app.loan.application.LoanService
import com.inclufin.backend.app.loan.domain.exception.MissingCapitalContributionException
import com.inclufin.backend.app.loan.domain.model.CapitalContributionType
import com.inclufin.backend.app.loan.domain.model.LoanRequest
import com.inclufin.backend.app.loan.domain.model.PaymentPlan
import com.inclufin.backend.app.loan.domain.service.TraditionalPaymentCalculator
import com.inclufin.backend.app.loan.domain.service.ReducedTermPaymentCalculator
import com.inclufin.backend.app.loan.domain.service.ReducedRatePaymentCalculator
import org.springframework.stereotype.Service

@Service
class LoanServiceImpl(
    private val traditionalPaymentCalculator: TraditionalPaymentCalculator,
    private val reducedTermPaymentCalculator: ReducedTermPaymentCalculator,
    private val reducedRatePaymentCalculator: ReducedRatePaymentCalculator
) : LoanService {

    override fun calculateTraditionalPaymentPlan(loanRequest: LoanRequest): PaymentPlan {
        return traditionalPaymentCalculator.calculatePaymentPlan(loanRequest)
    }

    override fun calculatePaymentPlanWithCapitalContribution(loanRequest: LoanRequest) = buildList {
        val contributionTypes = loanRequest.capitalContribution?.types ?: throw MissingCapitalContributionException(
            "Invalid capital contribution type"
        )
        add(calculateTraditionalPaymentPlan(loanRequest))
        if (contributionTypes.contains(CapitalContributionType.ALL)) {
            add(reducedTermPaymentCalculator.calculatePaymentPlan(loanRequest))
            add(reducedRatePaymentCalculator.calculatePaymentPlan(loanRequest))
        } else if (contributionTypes.contains(CapitalContributionType.REDUCED_TERM))
            add(reducedTermPaymentCalculator.calculatePaymentPlan(loanRequest))
        else if (contributionTypes.contains(CapitalContributionType.REDUCED_RATE))
            add(reducedRatePaymentCalculator.calculatePaymentPlan(loanRequest))
    }

    override fun calculateAllPaymentScenarios(loanRequest: LoanRequest) = buildList {
        add(calculateTraditionalPaymentPlan(loanRequest))
        add(reducedTermPaymentCalculator.calculatePaymentPlan(loanRequest))
        add(reducedRatePaymentCalculator.calculatePaymentPlan(loanRequest))
    }
}
