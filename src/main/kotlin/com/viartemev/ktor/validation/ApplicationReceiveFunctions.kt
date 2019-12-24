package com.viartemev.ktor.validation

import io.ktor.application.ApplicationCall
import io.ktor.request.receive
import io.ktor.request.receiveOrNull
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType

suspend inline fun <reified T : Any> ApplicationCall.receiveValidated(): Pair<T, ValidationResult> =
    receive<T>() to (request.call.attributes.getOrNull(ValidationFeatureKey) ?: ValidationResult.NotValidated)

suspend fun <T : Any> ApplicationCall.receiveValidated(type: KClass<T>): Pair<T, ValidationResult> =
    receive<T>(type.starProjectedType) to (request.call.attributes.getOrNull(ValidationFeatureKey)
        ?: ValidationResult.NotValidated)

suspend fun <T : Any> ApplicationCall.receiveValidated(type: KType): Pair<T, ValidationResult> =
    receive<T>(type) to (request.call.attributes.getOrNull(ValidationFeatureKey) ?: ValidationResult.NotValidated)

suspend inline fun <reified T : Any> ApplicationCall.receiveOrNullValidated(): Pair<T?, ValidationResult> =
    receiveOrNull<T>() to (request.call.attributes.getOrNull(ValidationFeatureKey) ?: ValidationResult.NotValidated)

suspend fun <T : Any> ApplicationCall.receiveOrNullValidated(type: KClass<T>): Pair<T?, ValidationResult> =
    receiveOrNull<T>(type.starProjectedType) to (request.call.attributes.getOrNull(ValidationFeatureKey)
        ?: ValidationResult.NotValidated)

suspend fun <T : Any> ApplicationCall.receiveOrNullValidated(type: KType): Pair<T?, ValidationResult> =
    receiveOrNull<T>(type) to (request.call.attributes.getOrNull(ValidationFeatureKey) ?: ValidationResult.NotValidated)