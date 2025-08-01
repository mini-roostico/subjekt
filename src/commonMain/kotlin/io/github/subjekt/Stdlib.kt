/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.core.Parameter
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.value.StringValue
import io.github.subjekt.core.value.Type
import io.github.subjekt.core.value.Value
import kotlin.math.min

/**
 * Object containing standard library functions that can be used as [SubjektFunction]s.
 */
val stdLibFunctions: List<SubjektFunction> =
    listOf(
        SubjektFunction("capitalizeFirst", ::capitalizeFirst),
        SubjektFunction("prettify", ::prettify),
    )

private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

/**
 * Object containing standard library parameters that can be used as [Parameter]s.
 */
val stdLibParameters: List<Parameter> =
    listOf(
        Parameter("DAYS_OF_WEEK", daysOfWeek.map { Value.ofString(it) }),
    )

/**
 * Capitalizes the first letter of a string.
 */
fun capitalizeFirst(str: List<Value>): Value {
    require(str.size == 1 && str.first() is StringValue) {
        "capitalizeFirst expects a single string argument"
    }
    val stringValue = str.first() as StringValue
    return StringValue(stringValue.value.replaceFirstChar(Char::titlecase))
}

/**
 * Prettifies [arguments] by removing all non-alphanumeric characters and joining them together following a Pascal
 * case notation.
 */
fun prettify(arguments: List<Value>): StringValue {
    require(arguments.isNotEmpty() && arguments.all { it.type == Type.STRING }) {
        "prettify expects a non-empty list of strings"
    }

    return StringValue(
        arguments
            .map { (it as StringValue).value }
            .joinToString("") { idFromCode(it) },
    )
}

private fun String.substringStartingFromFirstValidChar(): String {
    val startIndex = indexOfFirst { it.isLetter() }
    return if (startIndex != -1) substring(startIndex) else ""
}

private fun String.substringUntilFirstInvalidChar(): String {
    val endIndex = indexOfFirst { !it.isLetter() }
    return if (endIndex != -1) substring(0, endIndex) else this
}

/**
 * Obtains an identifier from a code snippet.
 */
private fun idFromCode(
    code: String,
    maxLength: Int = 50,
): String =
    code
        .substringStartingFromFirstValidChar()
        .trim()
        .substringUntilFirstInvalidChar()
        .trim()
        .run {
            substring(0, min(maxLength, length))
        }.replace("[^a-zA-Z0-9]".toRegex(), "")
        .replaceFirstChar(Char::titlecase)
