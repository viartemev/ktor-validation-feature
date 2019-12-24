package com.viartemev.ktor.validation

import io.ktor.application.ApplicationCall
import io.ktor.request.receive

suspend inline fun <reified T : Any> ApplicationCall.receiveValidated(): Pair<T, ValidationResult> {
    val receive = receive<T>()
    return receive to request.call.attributes[ValidationFeatureKey]
}