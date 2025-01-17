/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.parsing

import io.github.subjekt.core.Macro
import io.github.subjekt.core.Macro.Companion.asMacroDefinition
import io.github.subjekt.core.Macro.Companion.toMacro
import io.github.subjekt.core.MacroDefinition
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.Parameter.Companion.toParameter
import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Subject.Companion.createAndAddSubjectFromString
import io.github.subjekt.core.Suite
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.parsing.SuiteFactory.SubjectBuilder
import io.github.subjekt.core.parsing.SuiteFactory.SuiteBuilder
import io.github.subjekt.utils.Utils.checkNulls

/**
 * Visitor class used to parse a map into a [Suite] instance.
 */
internal class MapVisitor {
    /**
     * Exception thrown when an error occurs during parsing.
     */
    class ParsingException(
        override val message: String?,
    ) : Exception()

    private var suiteBuilder: SuiteBuilder = SuiteBuilder()
    private var subjectBuilder: SubjectBuilder = SubjectBuilder()
    private var suiteSymbolTable: SymbolTable = SymbolTable()
    private var subjectSymbolTable: SymbolTable = SymbolTable()

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
    @Throws(IllegalArgumentException::class, ParsingException::class)
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
            suiteBuilder.symbolTable(suiteSymbolTable).build()
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
            is Map<*, *> -> {
                subjectBuilder = SubjectBuilder()
                subject.entries.forEach { (key, value) ->
                    parsingCheck(value != null) { "Subject values must not be null" }
                    visitSubjectLevel(key.toString(), value!!)
                }
                subjectBuilder = subjectBuilder.id(suiteBuilder.getFreshSubjectId()).symbolTable(subjectSymbolTable)
                suiteBuilder = suiteBuilder.subject(subjectBuilder.build())
            }
            is String ->
                with(suiteBuilder.configurationSnapshot) {
                    suiteBuilder.createAndAddSubjectFromString(
                        subject,
                        expressionPrefix = expressionPrefix,
                        expressionSuffix = expressionSuffix,
                    )
                }
        }
    }

    private fun visitSubjectLevel(
        key: String,
        value: Any,
    ) {
        when (key) {
            in Subject.SUBJECT_NAME_KEYS ->
                with(suiteBuilder.configurationSnapshot) {
                    subjectBuilder =
                        subjectBuilder.name(Resolvable(value.toString(), expressionPrefix, expressionSuffix))
                }
            in SUITE_MACROS_KEYS -> visitMacros(value, insideSubject = true)
            in SUITE_PARAMS_KEYS -> visitParameters(value, insideSubject = true)
            else ->
                with(suiteBuilder.configurationSnapshot) {
                    subjectBuilder.field(
                        key,
                        Resolvable(value.toString(), expressionPrefix, expressionSuffix),
                    )
                }
        }
    }

    private fun visitImports(imports: Any) {
        TODO("Not yet implemented")
        println(imports)
    }

    private fun visitMacros(
        macros: Any,
        insideSubject: Boolean = false,
    ) {
        parsingCheck(macros is Map<*, *> || macros is List<*>) { "Macros must be a map or a list" }
        val parsedMacros =
            if (macros is List<*>) {
                macros.checkNulls().map { visitMacro(it) }
            } else {
                listOf(visitMacro(macros))
            }
        if (insideSubject) {
            subjectSymbolTable = subjectSymbolTable.defineMacros(parsedMacros)
        } else {
            suiteSymbolTable = suiteSymbolTable.defineMacros(parsedMacros)
        }
    }

    private fun visitMacro(macro: Any): Macro {
        parsingCheck(macro is Map<*, *>) { "Macro must be a map" }
        val macroMap = macro as Map<*, *>
        var macroDefinition: MacroDefinition? = null
        var macroResolvables: List<Resolvable>? = null
        val expressionPrefix = suiteBuilder.configurationSnapshot.expressionPrefix
        val expressionSuffix = suiteBuilder.configurationSnapshot.expressionSuffix
        macroMap.entries.forEach { (key, value) ->
            parsingCheck(value != null) { "Macro values must not be null" }
            when (key) {
                in Macro.MACRO_NAME_KEYS -> {
                    parsingCheck(value is String) { "Macro ID must be a string" }
                    macroDefinition = (value as String).asMacroDefinition()
                }
                in Macro.MACRO_RESOLVABLES_KEYS -> {
                    parsingCheck(value is List<*> || value is String) { "Macro resolvables must be a list" }
                    macroResolvables =
                        if (value is List<*>) {
                            value.map { Resolvable(it.toString(), expressionPrefix, expressionSuffix) }
                        } else {
                            listOf(Resolvable(value.toString(), expressionPrefix, expressionSuffix))
                        }
                }
                else -> parsingFail { "Unknown macro key: $key" }
            }
        }
        return macroDefinition?.toMacro(
            macroResolvables ?: parsingFail { "Macros must have at least one resolvable value" },
        )
            ?: parsingFail { "Macro definition failed" }
    }

    private fun visitParameters(
        parameters: Any,
        insideSubject: Boolean = false,
    ) {
        parsingCheck(parameters is Map<*, *> || parameters is List<*>) { "Parameters must be a map or a list" }
        val parsedParameters =
            if (parameters is List<*>) {
                parameters.checkNulls().map { visitParameter(it) }
            } else {
                listOf(visitParameter(parameters))
            }
        if (insideSubject) {
            subjectSymbolTable = subjectSymbolTable.defineParameters(parsedParameters)
        } else {
            suiteSymbolTable = suiteSymbolTable.defineParameters(parsedParameters)
        }
    }

    private fun visitParameter(parameter: Any): Parameter {
        parsingCheck(parameter is Map<*, *>) { "Parameter must be a map" }
        val parameterMap = parameter as Map<*, *>
        var parameterId: String? = null
        var parameterValues: List<String>? = null
        parameterMap.entries.forEach { (key, value) ->
            parsingCheck(value != null) { "Parameter values must not be null" }
            when (key) {
                in Parameter.PARAMETER_NAME_KEYS -> {
                    parsingCheck(value is String) { "Parameter's $key (i.e. ID) must be a string" }
                    parameterId = value as String
                }
                in Parameter.PARAMETER_VALUES_KEYS -> {
                    parsingCheck(value is List<*>) { "Parameter's $key (i.e. values) must be a list" }
                    parameterValues = (value as List<*>).map { it.toString() }
                }
                else -> parsingFail { "Unknown parameter key: $key" }
            }
        }
        return Pair(
            parameterId ?: parsingFail { "Parameter's ID is required" },
            parameterValues ?: parsingFail { "Parameters must have at least one value" },
        ).toParameter()
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
