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
        get() = this[DEFAULT_CODE_PREAMBLE_KEY] as? String ?: DEFAULT_CODE_PREAMBLE

    /**
     * Prefix used to identify the start of an expression.
     */
    val expressionPrefix: String
        get() = this[DEFAULT_EXPRESSION_PREFIX_KEY] as? String ?: DEFAULT_EXPRESSION_PREFIX

    /**
     * Suffix used to identify the end of an expression.
     */
    val expressionSuffix: String
        get() = this[DEFAULT_EXPRESSION_SUFFIX_KEY] as? String ?: DEFAULT_EXPRESSION_SUFFIX

    /**
     * Whether to lint the suite or not.
     */
    val lint: Boolean
        get() = (this[DEFAULT_LINT_KEY] as? String)?.toBooleanStrictOrNull() == !DEFAULT_LINT

    /**
     * Sets the value of the given key. Returns `true` if the key is among the default ones, `false` if it's a custom
     * key instead.
     */
    fun set(
        key: String,
        value: Any,
    ): Boolean {
        when (key) {
            in CODE_PREAMBLE_KEYS -> this[DEFAULT_CODE_PREAMBLE_KEY] = value
            in EXPRESSION_PREFIX_KEYS -> this[DEFAULT_EXPRESSION_PREFIX_KEY] = value
            in EXPRESSION_SUFFIX_KEYS -> this[DEFAULT_EXPRESSION_SUFFIX_KEY] = value
            in LINT_KEYS -> this[DEFAULT_LINT_KEY] = value
            else -> return false
        }
        return true
    }

    /**
     * Clones the configuration.
     */
    fun clone(): Configuration {
        val clone = Configuration()
        clone.putAll(this)
        return clone
    }

    /**
     * Companion object used to define default values.
     */
    companion object {
        private const val DEFAULT_CODE_PREAMBLE_KEY = "codePreamble"

        /**
         * Default code preamble.
         */
        internal const val DEFAULT_CODE_PREAMBLE = ""

        /**
         * Keys used to identify the code preamble.
         */
        internal val CODE_PREAMBLE_KEYS = setOf(DEFAULT_CODE_PREAMBLE_KEY, "code_preamble", "code-preamble", "preamble")

        private const val DEFAULT_EXPRESSION_PREFIX_KEY = "expressionPrefix"

        /**
         * Default expression prefix.
         */
        internal const val DEFAULT_EXPRESSION_PREFIX = "\${{"

        /**
         * Keys used to identify the expression prefix.
         */
        internal val EXPRESSION_PREFIX_KEYS =
            setOf(DEFAULT_EXPRESSION_PREFIX_KEY, "expression_prefix", "expression-prefix", "prefix")

        private const val DEFAULT_EXPRESSION_SUFFIX_KEY = "expressionSuffix"

        /**
         * Default expression suffix.
         */
        internal const val DEFAULT_EXPRESSION_SUFFIX = "}}"

        /**
         * Keys used to identify the expression suffix.
         */
        internal val EXPRESSION_SUFFIX_KEYS =
            setOf(DEFAULT_EXPRESSION_SUFFIX_KEY, "expression_suffix", "expression-suffix", "suffix")

        private const val DEFAULT_LINT_KEY = "lint"

        /**
         * Default value for linting.
         */
        internal const val DEFAULT_LINT = false

        /**
         * Keys used to identify whether to lint the suite or not.
         */
        internal val LINT_KEYS =
            setOf(
                DEFAULT_LINT_KEY,
                "linting",
                "enableLinting",
                "enable_linting",
                "enable-linting",
                "lintingOn",
                "lintOn",
                "lint-on",
                "linting-on",
                "linting_on",
                "lint_on",
            )
    }
}
