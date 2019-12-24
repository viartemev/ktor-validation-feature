package com.viartemev.ktor.validation

import com.viartemev.ktor.validation.exception.RequestValidationException
import com.viartemev.ktor.validation.exception.ValidationException
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.call
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.AttributeKey
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.io.ByteReadChannel
import org.slf4j.Logger
import org.slf4j.LoggerFactory


private val logger: Logger = LoggerFactory.getLogger("com.viartemev.ktor.validation.ValidationFeature")

@KtorExperimentalAPI
class ValidationFeature(configuration: Configuration) {
    private val validators = configuration.validators
    private val throwExceptionIfInvalid = configuration.throwExceptionIfInvalid

    class Configuration {
        var validators: List<Validator> = emptyList()
        var throwExceptionIfInvalid = true
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, ValidationFeature> {
        override val key: AttributeKey<ValidationFeature> = AttributeKey("ValidationFeature")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): ValidationFeature {
            val configuration = Configuration().apply(configure)
            val feature = ValidationFeature(configuration)

            if (feature.validators.isEmpty()) {
                logger.warn("Validation feature is not configured: validators is empty")
                return feature // don't install interceptor
            }

            pipeline.receivePipeline.intercept(ApplicationReceivePipeline.After) { receive ->
                // skip if already transformed
                if (subject.value !is ByteReadChannel) return@intercept
                // skip if a byte channel has been requested so there is nothing to negotiate
                if (subject.type == ByteReadChannel::class) return@intercept

                val validationResult = try {
                    feature.validators
                        .find { it.supports(receive.type) }
                        ?.validate(receive.value)
                } catch (e: Throwable) {
                    throw ValidationException(e.localizedMessage)
                }

                call.request.call.attributes.put(ValidationFeatureKey, validationResult ?: ValidationResult.NotValidated)

                if (feature.throwExceptionIfInvalid && validationResult is ValidationResult.Invalid) {
                    throw RequestValidationException(validationResult)
                }

                proceedWith(ApplicationReceiveRequest(receive.typeInfo, receive.value, receive.reusableValue))
            }
            return feature
        }

    }
}

val ValidationFeatureKey = AttributeKey<ValidationResult>("Validation")