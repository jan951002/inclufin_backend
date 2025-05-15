package com.inclufin.backend.app.loan.api.controller

import com.inclufin.backend.app.core.ApiResponse
import com.inclufin.backend.app.loan.api.PaymentPlansResponseEntity
import com.inclufin.backend.app.loan.api.dto.CalculateLoanPaymentRequestDto
import com.inclufin.backend.app.loan.api.mapper.CalculateLoanRequestMapper
import com.inclufin.backend.app.loan.api.mapper.PaymentPlanMapper
import com.inclufin.backend.app.loan.application.LoanService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/loans")
class LoanController(
    private val calculateLoanRequestMapper: CalculateLoanRequestMapper,
    private val paymentPlanResponseMapper: PaymentPlanMapper,
    private val loanService: LoanService,
) {

    @PostMapping("/calculate-payment-plan")
    fun calculatePaymentPlan(@Valid @RequestBody request: CalculateLoanPaymentRequestDto): PaymentPlansResponseEntity {
        val loanRequest = calculateLoanRequestMapper.toDomain(request)
        val paymentPlan = loanService.calculateTraditionalPaymentPlan(loanRequest)
        val responseDto = paymentPlanResponseMapper.toDto(paymentPlan)
        return ResponseEntity.ok(ApiResponse(success = true, data = listOf(responseDto)))
    }

    @PostMapping("/apply-capital-contribution")
    fun applyCapitalContribution(@RequestBody request: CalculateLoanPaymentRequestDto): PaymentPlansResponseEntity {
        val loanRequest = calculateLoanRequestMapper.toDomain(request)
        val paymentPlans = loanService.calculatePaymentPlanWithCapitalContribution(loanRequest)
        val responseDtos = paymentPlans.map(paymentPlanResponseMapper::toDto)
        return ResponseEntity.ok(ApiResponse(success = true, data = responseDtos))
    }

    @PostMapping("/calculate-all-scenarios")
    fun calculateAllScenarios(@RequestBody request: CalculateLoanPaymentRequestDto): PaymentPlansResponseEntity {
        val loanRequest = calculateLoanRequestMapper.toDomain(request)
        val paymentPlans = loanService.calculateAllPaymentScenarios(loanRequest)
        val responseDtos = paymentPlans.map(paymentPlanResponseMapper::toDto)
        return ResponseEntity.ok(ApiResponse(success = true, data = responseDtos))
    }
}
