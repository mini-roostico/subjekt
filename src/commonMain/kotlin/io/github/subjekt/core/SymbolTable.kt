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
data class SymbolTable(
    /**
     * The parameters that can be used to resolve values in a Suite.
     */
    private val parameters: Map<String, Parameter> = emptyMap(),
    /**
     * The macros that can be used to resolve values in a Suite.
     */
    private val macros: Map<String, Macro> = emptyMap(),
    /**
     * The functions that can be used to resolve values in a Suite.
     *
     * **Important**: Functions can be defined only programmatically, and are not part of the Suite definition.
     */
    private val functions: Map<String, Function1<List<*>, List<*>>> = emptyMap(),
) {
    /**
     * Returns a new [SymbolTable] with both the symbols from this SymbolTable and the symbols from [symbolTable].
     */
    operator fun plus(symbolTable: SymbolTable): SymbolTable =
        SymbolTable(
            parameters + symbolTable.parameters,
            macros + symbolTable.macros,
            functions + symbolTable.functions,
        )

    /**
     * Returns a new [SymbolTable] with the [Parameter] [parameter] defined.
     */
    fun defineParameter(parameter: Parameter): SymbolTable = copy(parameters = parameters + (parameter.id to parameter))

    /**
     * Returns a new [SymbolTable] with the [Macro] [macro] defined.
     */
    fun defineMacro(macro: Macro): SymbolTable = copy(macros = macros + (macro.id to macro))

    /**
     * Returns a new [SymbolTable] with the [Function1] [function] defined.
     */
    fun defineFunction(
        id: String,
        function: Function1<List<*>, List<*>>,
    ): SymbolTable =
        copy(
            functions =
                functions + (id to function),
        )

    /**
     * Returns the [Parameter] associated to [id], or `null` if no such parameter can be resolved.
     */
    fun resolveParameter(id: String): Parameter? = parameters[id]

    /**
     * Returns the [Macro] associated to [id], or `null` if no such macro can be resolved.
     */
    fun resolveMacro(id: String): Macro? = macros[id]

    /**
     * Returns the [Function1] associated to [id], or `null` if no such function can be resolved.
     */
    fun resolveFunction(id: String): Function1<List<*>, List<*>>? = functions[id]
}
