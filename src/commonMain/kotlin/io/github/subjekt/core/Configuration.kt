/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

class Configuration : MutableMap<String, Any> by mutableMapOf<String, Any>() {
    /**
     * The code preamble of the suite.
     */
    val codePreamble: String
        get() = this["codePreamble"] as? String ?: ""

    /**
     * Prefix used to identify the start of an expression.
     */
    val expressionPrefix: String
        get() = this["expressionPrefix"] as? String ?: "\${{"

    /**
     * Suffix used to identify the end of an expression.
     */
    val expressionSuffix: String
        get() = this["expressionSuffix"] as? String ?: "}}"

    /**
     * Whether to lint the suite or not.
     */
    val lint: Boolean
        get() = (this["lint"] as? String)?.toBooleanStrictOrNull() != false // Default to true
}
