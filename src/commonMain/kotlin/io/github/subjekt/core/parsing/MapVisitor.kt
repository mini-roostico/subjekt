/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.parsing

import io.github.subjekt.core.Suite
import io.github.subjekt.core.parsing.SuiteFactory.SuiteBuilder
import io.github.subjekt.utils.Utils.checkNulls

/**
 * Visitor class used to parse a map into a [Suite] instance.
 */
class MapVisitor {
    /**
     * Exception thrown when an error occurs during parsing.
     */
    class ParsingException(
        override val message: String?,
    ) : Exception()

    private var suiteBuilder: SuiteBuilder = SuiteBuilder()

    /**
     * Throws a [ParsingException] with the given message.
     */
    private fun parsingFail(message: () -> String): Nothing = throw ParsingException(message())

    private fun parsingCheck(
        condition: Boolean,
        message: () -> String,
    ) {
        if (!condition) {
            parsingFail(message)
        }
    }

    /**
     * Visits a map and creates a [Suite] instance. This method is unsafe and should be encapsulated in a try-catch
     * block or `runCatching` block to handle parsing exceptions.
     */
    @Throws(IllegalArgumentException::class)
    fun visit(map: Map<String, Any>): Suite {
        map.entries
            .sortedWith(
                // the configuration block gets parsed first before anything else
                compareBy { entry ->
                    if (entry.key in SUITE_CONFIG_KEYS) 0 else 1
                },
            ).forEach { (key, value) ->
                visitGlobalLevel(key, value)
            }
        return runCatching {
            suiteBuilder.build()
        }.fold({
            it
        }) {
            parsingFail { "Invalid Suite structure: ${it.message}" }
        }
    }

    private fun visitGlobalLevel(
        key: String,
        value: Any,
    ) {
        when (key) {
            in SUITE_ID_KEYS -> visitSuiteId(value)
            in SUITE_CONFIG_KEYS -> visitConfiguration(value)
            in SUITE_PARAMS_KEYS -> visitParameters(value)
            in SUITE_MACROS_KEYS -> visitMacros(value)
            in SUITE_IMPORTS_KEYS -> visitImports(value)
            in SUITE_SUBJECTS_KEYS -> visitSubjects(key, value)
            else -> parsingFail { "Unknown Suite configuration key: $key" }
        }
    }

    private fun visitSuiteId(value: Any) {
        parsingCheck(value is String) { "Suite ID must be a string" }
        suiteBuilder = suiteBuilder.id(value.toString())
    }

    private fun visitConfiguration(config: Any) {
        parsingCheck(config is Map<*, *>) { "Configuration must be a map" }
        val configMap = config as Map<*, *>
        configMap.forEach { (key, value) ->
            parsingCheck(key is String) { "Configuration keys cannot be null" }
            parsingCheck(value != null) { "Configuration value for key '$key' cannot be null" }
            visitConfigurationLevel(key.toString(), value!!)
        }
    }

    private fun visitConfigurationLevel(
        key: String,
        value: Any,
    ) {
        suiteBuilder = suiteBuilder.addConfig(key, value)
    }

    private fun visitSubjects(
        key: String,
        subjects: Any,
    ) {
        parsingCheck(subjects is List<*> || subjects is Map<*, *> || subjects is String) {
            "'$key' value must either be a map, a list, or a string"
        }
        if (subjects is List<*>) {
            subjects.checkNulls().map { visitSubject(it) }
        } else {
            visitSubject(subjects)
        }
    }

    private fun visitSubject(subject: Any) {
        parsingCheck(subject is Map<*, *> || subject is String) { "Subject must be a map or a string" }
        when (subject) {
            is Map<*, *> -> TODO()
            is String -> TODO()
        }
    }

    private fun visitImports(imports: Any) {
        TODO("Not yet implemented")
        println(imports)
    }

    private fun visitMacros(macros: Any) {
        TODO("Not yet implemented")
        println(macros)
    }

    private fun visitParameters(parameters: Any) {
        TODO("Not yet implemented")
        println(parameters)
    }

    /**
     * Collection of known keys for the [Suite] configuration.
     */
    companion object {
        /**
         * Keys used to identify the suite's name.
         */
        internal val SUITE_ID_KEYS = setOf("name", "id")

        /**
         * Keys used to identify the suite's configuration.
         */
        internal val SUITE_CONFIG_KEYS = setOf("configuration", "config")

        /**
         * Keys used to identify the suite's parameters.
         */
        internal val SUITE_PARAMS_KEYS = setOf("parameters", "params")

        /**
         * Keys used to identify the suite's macros.
         */
        internal val SUITE_MACROS_KEYS = setOf("macros")

        /**
         * Keys used to identify the suite's imports.
         */
        internal val SUITE_IMPORTS_KEYS = setOf("imports", "import", "include")

        /**
         * Keys used to identify the suite's subjects.
         */
        internal val SUITE_SUBJECTS_KEYS = setOf("subjects", "subject")
    }
}
