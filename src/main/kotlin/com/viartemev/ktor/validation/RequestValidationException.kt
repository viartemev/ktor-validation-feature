package com.viartemev.ktor.validation

class RequestValidationException(val validationResult: ValidationResult) : RuntimeException()