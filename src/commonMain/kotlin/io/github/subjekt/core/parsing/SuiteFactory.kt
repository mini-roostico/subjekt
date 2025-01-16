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
import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Source
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Suite
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.utils.Logger.warning

/**
 * Factory class used to create [Suite] instances.
 */
object SuiteFactory {
    /**
     * Builder class used to create a [Suite] instance.
     */
    internal class SuiteBuilder {
        private var id: String? = null
        private var subjects: List<Subject> = emptyList()
        private var configuration: Configuration = Configuration()
        private var symbolTable: SymbolTable? = null

        /**
         * Gets a copy of the [Configuration] of the [Suite] that is being built.
         */
        val configurationSnapshot: Configuration = configuration.clone()

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
         * Generates a fresh [Subject] ID simply by returning the size of the [subjects] list.
         */
        fun getFreshSubjectId(): Int = subjects.size

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
            return Suite(
                id = id!!,
                subjects = subjects,
                configuration = TODO(),
                symbolTable = TODO(),
            )
        }
    }

    /**
     * Builder class used to create a [Subject] instance.
     */
    internal class SubjectBuilder {
        private var id: Int? = null
        private var fields: Map<String, Resolvable> = emptyMap()
        private var symbolTable: SymbolTable = SymbolTable()

        /**
         * Sets the ID of the [Subject].
         */
        fun id(id: Int): SubjectBuilder =
            apply {
                this.id = id
            }

        /**
         * Adds a field to the [Subject].
         */
        fun field(
            name: String,
            value: Resolvable,
        ): SubjectBuilder =
            apply {
                fields += name to value
            }

        /**
         * Sets the name of the [Subject]. Name is just a field like any other, but it's a common field that can be
         * used.
         */
        fun name(value: Resolvable): SubjectBuilder =
            apply {
                fields += Subject.DEFAULT_NAME_KEY to value
            }

        /**
         * Sets the symbol table of the [Subject].
         */
        fun symbolTable(symbolTable: SymbolTable): SubjectBuilder =
            apply {
                this.symbolTable = symbolTable
            }

        /**
         * Builds the [Subject] instance. This method is unsafe and should be encapsulated in a try-catch block or
         * `runCatching` block to handle [IllegalArgumentException].
         */
        fun build(): Subject {
            require(id != null) { "Subject ID is required" }
            require(fields.isNotEmpty()) { "At least one field is required" }
            return Subject(
                id = id!!,
                resolvables = fields,
                symbolTable = symbolTable,
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
