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
 * Represents a collection of [Parameter]s, [Macro]s, and [Function1]s that can be used to resolve values in a Suite.
 */
@ConsistentCopyVisibility
data class SymbolTable
    internal constructor(
        /**
         * The parameters that can be used to resolve values in a Suite.
         */
        val parameters: Map<String, Parameter> = emptyMap(),
        /**
         * The macros that can be used to resolve values in a Suite.
         */
        val macros: Map<String, Macro> = emptyMap(),
        /**
         * The functions that can be used to resolve values in a Suite.
         *
         * **Important**: Functions can be defined only programmatically, and are not part of the Suite definition.
         */
        val functions: Map<String, SubjektFunction> = emptyMap(),
    ) {
        /**
         * Returns a new [SymbolTable] with both the symbols from this SymbolTable and the symbols from [symbolTable].
         */
        operator fun plus(symbolTable: SymbolTable): SymbolTable =
            SymbolTable(
                parameters.toMutableMap().also {
                    symbolTable.parameters.forEach { (k, v) ->
                        it.put(k, v)
                    }
                },
                macros + symbolTable.macros,
                functions + symbolTable.functions,
            )

        /**
         * Returns a new [SymbolTable] with the [Parameter] [parameter] defined.
         */
        fun defineParameter(parameter: Parameter): SymbolTable =
            copy(
                parameters =
                    parameters + (parameter.id to parameter),
            )

        /**
         * Returns a new [SymbolTable] with the [Parameter]s [parameters] defined.
         */
        fun defineParameters(parameters: Iterable<Parameter>): SymbolTable =
            copy(
                parameters =
                    this.parameters + parameters.map { (it.id to it) },
            )

        /**
         * Returns a new [SymbolTable] with the [Macro] [macro] defined.
         */
        fun defineMacro(macro: Macro): SymbolTable =
            copy(
                macros =
                    macros + ((macro.id + ARGS_SEPARATOR + macro.argumentsIdentifiers.size) to macro),
            )

        /**
         * Returns a new [SymbolTable] with the [Macro]s [macros] defined.
         */
        fun defineMacros(macros: Iterable<Macro>): SymbolTable =
            copy(
                macros =
                    this.macros + macros.map { ((it.id + ARGS_SEPARATOR + it.argumentsIdentifiers.size) to it) },
            )

        /**
         * Returns a new [SymbolTable] with the [Function1] [function] defined.
         */
        fun defineFunction(
            id: String,
            function: Function1<List<String>, String>,
        ): SymbolTable =
            copy(
                functions =
                    functions + (id to SubjektFunction(id, function)),
            )

        /**
         * Returns the [Parameter] associated to [id], or `null` if no such parameter can be resolved.
         */
        fun resolveParameter(id: String): Parameter? = parameters[id]

        /**
         * Returns the [Macro] associated to [id], or `null` if no such macro can be resolved. Since Subjekt supports
         * a light overloading system, you must specify the [argsNumber] the macro has to correctly resolve it.
         */
        fun resolveMacro(
            id: String,
            argsNumber: Int = 0,
        ): Macro? = macros[id + ARGS_SEPARATOR + argsNumber]

        /**
         * Returns the [Macro]s associated to [id], or an empty list if no such macro can be resolved. This includes all
         * the overloaded versions of the macro.
         */
        fun resolveMacros(id: String): List<Macro> =
            macros
                .filterKeys {
                    it.substring(it.indexOfFirst { it.isDigit() } - 1) == id
                }.values
                .toList()

        /**
         * Returns the [Function1] associated to [id], or `null` if no such function can be resolved.
         */
        fun resolveFunction(id: String): SubjektFunction? = functions[id]

        companion object {
            /**
             * The separator used to separate the arguments of a macro during the ID resolution.
             */
            internal const val ARGS_SEPARATOR = "/"
        }
    }
