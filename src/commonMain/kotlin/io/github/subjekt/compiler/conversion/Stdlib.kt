/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.conversion

import kotlin.math.min

/**
 * Object containing standard library functions that can be used as macros.
 */
@SubjektModule("std")
object Stdlib {
    /**
     * Capitalizes the first letter of a string.
     */
    @JvmStatic
    @Macro("capitalizeFirst")
    fun capitalizeFirst(str: String): String = str.replaceFirstChar(Char::titlecase)

    /**
     * Prettifies [arguments] by removing all non-alphanumeric characters and joining them together following a Pascal case
     * notation.
     */
    @JvmStatic
    @Macro("prettify")
    fun prettify(vararg arguments: String): String = arguments.joinToString("") { idFromCode(it) }

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
}
