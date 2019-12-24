# Ktor validation feature

#### Examples:
Define custom validator:
```kotlin
object MessageValidator : Validator {

    override fun supports(clazz: KClass<*>): Boolean = Message::class == clazz

    override fun validate(obj: Any): ValidationResult {
        val errors = mapOf("name" to listOf("name should be bigger than 3"))
        return ValidationResult.Invalid(errors)
    }
}
```
#### If you would like throw RequestValidationException if request body is not valid:
```kotlin
install(ValidationFeature) {
    validators = listOf(MessageValidator)
    throwExceptionIfInvalid = true //default value is true
}

install(StatusPages) {
    exception<RequestValidationException> { cause ->
        call.respond(HttpStatusCode.BadRequest, cause.validationResult)
    }
}

post("/") {
    val (msg, validatedResult) = call.receive<Message>()
    call.respond("Done")
}
```
#### If you would like to get a resulted object with validation result:
```kotlin
install(ValidationFeature) {
    validators = listOf(MessageValidator)
    throwExceptionIfInvalid = false
}

post("/") {
    val (msg, validatedResult) = call.receiveValidated<Message>()
    when (validatedResult) {
        is ValidationResult.Valid -> call.respond("Done")
        is ValidationResult.Invalid -> call.respond(HttpStatusCode.BadRequest, "Request is invalid, errors: ${validatedResult.errors}")
    }
}
```

