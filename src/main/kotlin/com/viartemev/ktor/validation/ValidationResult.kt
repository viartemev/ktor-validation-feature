package com.viartemev.ktor.validation

typealias PropertyPath = String
typealias ValidationError = String

sealed class ValidationResult {
    class Invalid(val errors: Map<PropertyPath, List<ValidationError>> = HashMap()) : ValidationResult()
    object Valid : ValidationResult()
}
