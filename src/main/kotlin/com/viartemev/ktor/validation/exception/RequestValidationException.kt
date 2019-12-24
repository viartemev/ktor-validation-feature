package com.viartemev.ktor.validation.exception

import com.viartemev.ktor.validation.ValidationResult

class RequestValidationException(val validationResult: ValidationResult) : RuntimeException()