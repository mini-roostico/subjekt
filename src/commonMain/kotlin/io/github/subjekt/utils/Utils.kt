/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.utils

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
}
