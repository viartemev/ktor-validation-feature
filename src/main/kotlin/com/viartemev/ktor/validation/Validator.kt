package com.viartemev.ktor.validation

import kotlin.reflect.KClass


interface Validator<T> {
    @Suppress("UNCHECKED_CAST")
    fun castAndValidate(any: Any): ValidationResult = validate(any as T)
    fun validate(obj: T): ValidationResult
    fun supports(clazz: KClass<*>): Boolean
}