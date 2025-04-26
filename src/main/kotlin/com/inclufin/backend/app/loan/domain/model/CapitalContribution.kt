package com.inclufin.backend.app.loan.domain.model

import java.math.BigDecimal

data class CapitalContribution(
    val startMonth: Int,
    val contributionAmount: BigDecimal,
    val type: CapitalContributionType = CapitalContributionType.PRINCIPAL_REDUCTION
)
