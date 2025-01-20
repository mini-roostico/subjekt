/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.definition

import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.SymbolTable.Companion.ARGS_SEPARATOR

/**
 * A context is a collection of defined parameters and macros. It can be seen as a possible instance of the resolution
 * of a [io.github.subjekt.core.SymbolTable]'s symbols.
 *
 * Within one fixed context, the [io.github.subjekt.core.Resolvable] can be resolved to a single
 * [io.github.subjekt.core.resolution.Instance].
 */
data class Context(
    val definedParameters: Map<String, DefinedParameter>,
    val definedMacros: Map<String, DefinedMacro>,
    val functions: Map<String, SubjektFunction>,
) {
    /**
     * Creates a new empty context
     */
    constructor() : this(emptyMap(), emptyMap(), emptyMap())

    /**
     * Returns the [DefinedParameter] with the given [id], if it exists in this context.
     */
    fun lookupParameter(id: String): DefinedParameter? = definedParameters[id]

    /**
     * Returns the [DefinedMacro] with the given [id] and [nArgs], if it exists in this context.
     */
    fun lookupMacro(
        id: String,
        nArgs: Int = 0,
    ): DefinedMacro? = definedMacros[id + ARGS_SEPARATOR + nArgs]

    /**
     * Returns the [Function1] with the given [id], if it exists in this context.
     */
    fun lookupFunction(id: String): SubjektFunction? = functions[id]

    /**
     * Returns a new context with the given [DefinedParameter] added to it.
     */
    fun withParameter(
        id: String,
        value: String,
    ): Context = copy(definedParameters = definedParameters + (id to DefinedParameter(id, value)))

    /**
     * Returns a new context with the given [DefinedParameter]s (id -> value) added to it.
     */
    fun withParameters(vararg parameters: Pair<String, String>): Context =
        copy(
            definedParameters =
                definedParameters + parameters.map { (id, value) -> id to DefinedParameter(id, value) },
        )

    /**
     * Returns a new context with the given [DefinedMacro] added to it.
     */
    fun withMacro(
        id: String,
        argIds: List<String>,
        value: Resolvable,
    ): Context =
        copy(definedMacros = definedMacros + (id + ARGS_SEPARATOR + argIds.size to DefinedMacro(id, argIds, value)))

    /**
     * Returns a new context with the given [DefinedMacro]s added to it.
     */
    fun withMacros(vararg macros: DefinedMacro): Context =
        copy(
            definedMacros =
                definedMacros + macros.associateBy { it.macroId + ARGS_SEPARATOR + it.argumentsIdentifiers.size },
        )

    /**
     * Returns a new context with the given [Function1] added to it.
     */
    fun withFunction(
        id: String,
        function: Function1<List<String>, String>,
    ): Context = copy(functions = functions + (id to SubjektFunction(id, function)))

    companion object {
        /**
         * An empty context.
         */
        val empty: Context = Context(emptyMap(), emptyMap(), emptyMap())
    }
}
