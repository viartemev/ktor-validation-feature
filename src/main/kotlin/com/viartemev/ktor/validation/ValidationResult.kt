package com.viartemev.ktor.validation

typealias PropertyPath = String
typealias ValidationError = String

sealed class ValidationResult {
    object NotValidated : ValidationResult()
    object Valid : ValidationResult()
    class Invalid(private val errors: Map<PropertyPath, List<ValidationError>>) : ValidationResult()
}
