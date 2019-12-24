# Ktor validation feature
[![Build Status](https://travis-ci.com/viartemev/ktor-validation-feature.svg?branch=master)](https://travis-ci.com/viartemev/ktor-validation-feature)

Validation is important part of each API. This feature provides a simple way for incoming request validation.

Let's imagine we have request object which must be validated:
```kotlin
data class Message(val id: Long, val message: String)
```
First of all, we need to implement validation logic implementing `Validator` interface:
```kotlin
object MessageValidator : Validator<Message> {

    override fun supports(clazz: KClass<*>): Boolean = Message::class == clazz

    override fun validate(obj: Message): ValidationResult {
        val errors = mutableMapOf<PropertyPath, List<ValidationError>>()
        if (obj.message.isBlank()) errors["message"] = listOf("message must be not blank")
        return ValidationResult.Invalid(errors.toMap())
    }
}
```
Then we need to configure the feature. There are 2 options:
1. If object validation fails (object is not valid), then we get `RequestValidationException`, which we need handle.
One of the options is StatusPage feature.
```kotlin
install(ValidationFeature) {
    validators = listOf(MessageValidator) //add MessageValidator to feature
}

//StatusPages feature is optional, otherwise you should care about exception handling by yourself
install(StatusPages) {
    exception<RequestValidationException> { cause -> 
        call.respond(HttpStatusCode.BadRequest, cause.validationResult) 
    }
}

// Some logic is here
post("/messages") {
    val message = call.receive<Message>()
    call.respond(message)
}
```
2. If you don't want to throw exception, you should set `throwExceptionIfInvalid` to `false`. 
In this case, you could get validation result by calling: `call.receiveValidated<T>()` which return `Pair<T, ValidationResult>`.
`ValidationResult` is sealed class, can be:
    - `NotValidated` - in case validators weren't provided
    - `Valid` - in case validation finished successfully
    - `Invalid` - in case validation failed
```kotlin
install(ValidationFeature) {
    validators = listOf(MessageValidator) //add MessageValidator to feature
    throwExceptionIfInvalid = false //by default, it is true
}

post("/messages") {
    val (msg, validatedResult) = call.receiveValidated<Message>()
    when (validatedResult) {
        is ValidationResult.NotValidated -> call.respond(msg.message)
        is ValidationResult.Valid -> call.respond(msg)
        is ValidationResult.Invalid -> call.respond(HttpStatusCode.BadRequest, validatedResult.errors)    
    }
}
```

