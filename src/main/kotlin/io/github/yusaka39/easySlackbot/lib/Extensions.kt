package io.github.yusaka39.easySlackbot.lib

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KType

private inline fun <reified T> T?.validateNullability(isNullable: Boolean): T? {
    if (this != null) {
        return this
    }
    if (isNullable) {
        return null
    }
    throw IllegalArgumentException("$this cannot convert to ${T::class.simpleName}.")
}

internal fun String.convertTo(kType: KType): Any? {
    val isNullable = kType.isMarkedNullable
    return when (kType.classifier) {
        Byte::class -> this.toByteOrNull().validateNullability(isNullable)
        Short::class -> this.toShortOrNull().validateNullability(isNullable)
        Int::class -> this.toIntOrNull().validateNullability(isNullable)
        Long::class -> this.toLongOrNull().validateNullability(isNullable)
        Float::class -> this.toFloatOrNull().validateNullability(isNullable)
        Double::class -> this.toDoubleOrNull().validateNullability(isNullable)
        Boolean::class -> when (this) {
            "true" -> true
            "false" -> false
            else -> null
        }.validateNullability(isNullable)
        String::class -> this
        else -> throw IllegalArgumentException("${kType.classifier} is not allowed as parameter")
    }
}

internal inline fun <reified T: Annotation> KAnnotatedElement.isAnnotatedWith() = this.annotations.any { it is T }