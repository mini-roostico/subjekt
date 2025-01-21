/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.utils

import io.github.subjekt.core.parsing.MapVisitor.ParsingException
import io.github.subjekt.core.resolution.ResolvedSubject

object Utils {
    /**
     * Checks that the map does not contain null keys or values, and throws an [IllegalArgumentException] if it does.
     */
    @Throws(IllegalArgumentException::class)
    internal fun <K, V> Map<K?, V?>.checkNulls(): Map<K, V> =
        map { (key, value) ->
            require(key != null && value != null) { "Cannot use null values in Subjekt: $key -> $value" }
            key to value
        }.toMap()

    /**
     * Checks that the map does not contain null values, and throws an [IllegalArgumentException] if it does.
     */
    @Throws(IllegalArgumentException::class)
    internal fun <T> List<T?>.checkNulls(): List<T> =
        map { value ->
            require(value != null) { "Cannot use null values in Subjekt: $value" }
            value
        }

    /**
     * Checks if a string is a legal identifier. A legal identifier must start with a letter and can contain letters,
     * digits and [legalSymbols]. By default, '_' is the only legal symbol.
     */
    internal fun String.isLegalIdentifier(vararg legalSymbols: Char = charArrayOf('_')): Boolean =
        get(0).isLetter() &&
            all {
                it.isLetterOrDigit() || it in legalSymbols
            }

    /**
     * Simple implementation of the `format` method available on the JVM. It replaces all occurrences of `{{n}}` with
     * the `n`-th element of the `args` array.
     */
    internal fun String.format(vararg args: Any?): String = format(args.toList())

    /**
     * Simple implementation of the `format` method available on the JVM. It replaces all occurrences of `{{n}}` with
     * the `n`-th element of the `args` array.
     */
    internal fun String.format(args: List<Any?>): String {
        var result = this
        val regex = Regex("""\{\{(\d+)\}\}""")
        regex.findAll(this).forEach { matchResult ->
            val index = matchResult.groupValues[1].toIntOrNull()
            if (index != null && index in args.indices) {
                val replacement = args[index]?.toString() ?: ""
                result = result.replace(matchResult.value, replacement)
            }
        }
        return result
    }

    /**
     * Builds a regex pattern that matches the given expression within prefix and suffix.
     */
    internal fun buildRegex(
        expressionPrefix: String,
        expressionSuffix: String,
    ): Regex {
        val escapedPrefix = Regex.escape(expressionPrefix)
        val escapedSuffix = Regex.escape(expressionSuffix)
        return Regex("$escapedPrefix([\\s\\S]*?)$escapedSuffix", RegexOption.MULTILINE)
    }

    /**
     * Throws a [ParsingException] with the given message.
     */
    internal fun parsingFail(message: () -> String): Nothing = throw ParsingException(message())

    internal fun parsingCheck(
        condition: Boolean,
        message: () -> String,
    ) {
        if (!condition) {
            parsingFail(message)
        }
    }

    /**
     * Utility function to generate a unique name for a [ResolvedSubject].
     */
    internal fun ResolvedSubject.uniqueName(): String = "Subject_${subjectId}_${hashCode()}"
}
