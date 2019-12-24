package com.viartemev.ktor.validation

import kotlin.reflect.KClass

interface Validator {
    fun validate(obj: Any): ValidationResult
    fun supports(clazz: KClass<*>): Boolean
}