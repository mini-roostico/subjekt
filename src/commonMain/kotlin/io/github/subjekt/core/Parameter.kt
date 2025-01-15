/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

/**
 * Represents a parameter in a Suite or Subject. Parameters are used to define the values that can be used in a Suite or
 * Subject.
 *
 * Note: differently from the macros, parameters values are not [Resolvable]s, so their value is always a constant.
 */
data class Parameter(
    /**
     * The unique identifier of the Parameter. This is used to reference the Parameter in the symbol table.
     */
    val id: String,
    /**
     * The values that the parameter can assume. These are the possible values that can be used in the [Resolvable]s.
     */
    val values: List<String>,
) {
    companion object {
        /**
         * Keys that can be used as synonyms for [DEFAULT_ID_KEY].
         */
        val PARAMETER_NAME_KEYS = setOf("name", "id", "identifier", "title")

        /**
         * The default key used to indicate the ID of the parameter.
         */
        const val DEFAULT_ID_KEY = "id"

        /**
         * Keys that can be used as synonyms for [DEFAULT_VALUES_KEY].
         */
        val PARAMETER_VALUES_KEYS = setOf("values", "value", "val", "v", "bodies")

        /**
         * The default key used to indicate the values of the parameter.
         */
        const val DEFAULT_VALUES_KEY = "values"

        /**
         * Utility function to create a [Parameter] from a pair of ID and value.
         */
        fun Pair<String, *>.toParameter(): Parameter {
            val (id, value) = this
            return Parameter(id, listOf(value.toString()))
        }

        /**
         * Utility function to create a [Parameter] from a pair of ID and list of values.
         */
        fun Pair<String, List<*>>.toParameter(): Parameter {
            val (id, values) = this
            return Parameter(id, values.map { it.toString() })
        }
    }
}
