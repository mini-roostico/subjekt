/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.parsing

import io.github.subjekt.core.Configuration
import io.github.subjekt.core.Source
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Suite
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.impl.SuiteImpl
import io.github.subjekt.utils.Logger.warning

/**
 * Factory class used to create [Suite] instances.
 */
object SuiteFactory {
    /**
     * Builder class used to create a [Suite] instance.
     */
    class SuiteBuilder {
        private var id: String? = null
        private var subjects: List<Subject> = emptyList()
        private var configuration: Configuration = Configuration()
        private var symbolTable: SymbolTable? = null

        /**
         * Sets the ID of the [Suite].
         */
        fun id(id: String): SuiteBuilder =
            apply {
                this.id = id
            }

        /**
         * Adds a [Subject] to the [Suite].
         */
        fun subject(subject: Subject): SuiteBuilder =
            apply {
                subjects += subject
            }

        /**
         * Adds multiple [Subject]s to the [Suite].
         */
        fun subjects(vararg subject: Subject): SuiteBuilder =
            apply {
                subjects += subject
            }

        /**
         * Sets the [Configuration] of the [Suite].
         */
        fun configuration(configuration: Configuration): SuiteBuilder =
            apply {
                this.configuration = configuration
            }

        /**
         * Adds a configuration key-value pair to the [Suite].
         */
        fun addConfig(
            key: String,
            value: Any,
        ): SuiteBuilder =
            apply {
                val default = configuration.set(key, value)
                if (!default) {
                    warning {
                        "Adding a non-default configuration key '$key' to the suite."
                    }
                }
            }

        /**
         * Sets the [SymbolTable] of the [Suite].
         */
        fun symbolTable(symbolTable: SymbolTable): SuiteBuilder =
            apply {
                this.symbolTable = symbolTable
            }

        /**
         * Builds the [Suite] instance. This method is unsafe and should be encapsulated in a try-catch block or
         * `runCatching` block to handle [IllegalArgumentException].
         */
        fun build(): Suite {
            require(id != null) { "Suite ID is required" }
            require(subjects.isNotEmpty()) { "At least one subject is required" }
            return SuiteImpl(
                id = id!!,
                subjects = subjects,
                configuration = TODO(),
                symbolTable = TODO(),
            )
        }
    }

    /**
     * Parses the content of the [Source] (i.e., the map) into a [Suite], returning a [Result] with the [Suite] if
     * the parsing was successful, or failure if it failed.
     */
    fun Source.parseIntoSuite(): Result<Suite> =
        extract()
            .mapCatching {
                with(MapVisitor()) {
                    visit(it)
                }
            }
}
