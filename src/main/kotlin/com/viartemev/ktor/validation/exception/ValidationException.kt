package com.viartemev.ktor.validation.exception

class ValidationException(override val message: String) : RuntimeException(message)