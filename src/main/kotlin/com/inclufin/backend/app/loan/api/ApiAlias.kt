package com.inclufin.backend.app.loan.api

import com.inclufin.backend.app.core.ApiResponse
import com.inclufin.backend.app.loan.api.dto.PaymentPlanResponseDto
import org.springframework.http.ResponseEntity

typealias PaymentPlansResponseEntity = ResponseEntity<ApiResponse<List<PaymentPlanResponseDto>>>
