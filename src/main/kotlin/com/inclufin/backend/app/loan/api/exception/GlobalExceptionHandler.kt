package com.inclufin.backend.app.loan.api.exception

import com.inclufin.backend.app.core.ApiResponse
import com.inclufin.backend.app.core.ErrorResponse
import com.inclufin.backend.app.loan.domain.exception.MissingCapitalContributionException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = ex.bindingResult.fieldErrors.joinToString(", ") {
            "${it.field}: ${it.defaultMessage}"
        }
        val errorResponse = ErrorResponse(status.value(), "Invalid request parameters: $errors")
        val apiResponse = ApiResponse<Any>(success = false, error = errorResponse)
        return ResponseEntity(apiResponse, headers, status)
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errorMessage = "Invalid request body format or data type."
        val errorResponse = ErrorResponse(status.value(), errorMessage)
        val apiResponse = ApiResponse<Any>(success = false, error = errorResponse)
        return ResponseEntity(apiResponse, headers, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ApiResponse<Any>> {
        logger.error("Unexpected error occurred", ex)
        val errorMessage = "An unexpected error occurred. Please try again later."
        val errorResponse = ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage)
        return ResponseEntity(
            ApiResponse(success = false, error = errorResponse),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(MissingCapitalContributionException::class)
    fun handleMissingCapitalContribution(
        ex: MissingCapitalContributionException,
        request: WebRequest
    ): ResponseEntity<ApiResponse<Any>> {
        val errorResponse = ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.message ?: "Missing capital contribution data."
        )
        return ResponseEntity(
            ApiResponse(success = false, error = errorResponse),
            HttpStatus.BAD_REQUEST
        )
    }
}
