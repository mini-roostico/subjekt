/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.yaml

/**
 * Serializable representation of a suite. It can be converted to a [io.github.subjekt.compiler.nodes.suite.Suite] node.
 */
data class Suite(
    /**
     * The name of the suite.
     */
    val name: String,
    /**
     * The configuration of the suite.
     */
    val config: Configuration?,
    /**
     * The list of macros defined in the suite.
     */
    val macros: List<Macro>?,
    /**
     * The list of subjects defined in the suite.
     */
    val subjects: List<Subject>?,
    /**
     * The list of parameters globally defined in the suite.
     */
    val parameters: List<Parameter>?,
    /**
     * The list of imports to be used in the suite.
     */
    val import: List<String>? = null,
)

/**
 * Serializable representation of a subject. It can be converted to a [io.github.subjekt.compiler.nodes.suite.Subject]
 * node.
 */
data class Subject(
    /**
     * The name of the subject.
     */
    val name: String,
    /**
     * The list of parameters locally defined in the subject.
     */
    val parameters: List<Parameter>?,
    /**
     * The list of macros locally defined in the subject.
     */
    val macros: List<Macro>?,
    /**
     * The code of the subject.
     */
    val code: String,
    /**
     * The list of outcomes of the subject.
     */
    val outcomes: List<Outcome>?,
    /**
     * Additional properties that can be used to store arbitrary key-value pairs.
     */
    val properties: Map<String, String>? = null,
)

/**
 * Serializable representation of a macro. It can be converted to a [io.github.subjekt.compiler.nodes.suite.Macro] node.
 */
data class Macro(
    /**
     * Textual definition of the macro in the form `macroName(param1, param2, ...)`.
     */
    val def: String,
    /**
     * The list of values of the macro. Each value is a body of the macro.
     */
    val values: List<String>?,
    /**
     * The value of the macro. This is used when the macro has only one body.
     */
    val value: String?,
)

/**
 * Serializable representation of a parameter. It can be converted to a
 * [io.github.subjekt.compiler.nodes.suite.Parameter] node.
 */
data class Parameter(
    /**
     * The identifier of the parameter.
     */
    val name: String,
    /**
     * The list of values of the parameter. Each parameter can have multiple values, but these can't be resolvable.
     * Instead, they must be already defined values.
     */
    val values: List<String>?,
    /**
     * The value of the parameter. This is used when the parameter has only one value.
     */
    val value: String?,
)

/**
 * Serializable representation of an outcome. It can be converted to a [io.github.subjekt.compiler.nodes.suite.Outcome]
 * node.
 */
data class Outcome(
    /**
     * The name of the outcome.
     */
    val warning: String?,
    /**
     * The code of the outcome.
     */
    val error: String?,
)

/**
 * Serializable representation of the configuration of a suite. It also stores the default values for the configuration.
 * It can be used to store arbitrary key-value pairs.
 */
class Configuration : MutableMap<String, String> by mutableMapOf<String, String>() {
    /**
     * The code preamble of the suite.
     */
    val codePreamble: String
        get() = this["codePreamble"] ?: ""

    /**
     * Prefix used to identify the start of an expression.
     */
    val expressionPrefix: String
        get() = this["expressionPrefix"] ?: "\${{"

    /**
     * Suffix used to identify the end of an expression.
     */
    val expressionSuffix: String
        get() = this["expressionSuffix"] ?: "}}"

    /**
     * Whether to lint the suite or not.
     */
    val lint: Boolean
        get() = this["lint"]?.toBooleanStrictOrNull() != false // Default to true
}
