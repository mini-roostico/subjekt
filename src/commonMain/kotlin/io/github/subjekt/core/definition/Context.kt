/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.definition

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
) {
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
}
