/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions

/**
 * Represents a symbol that can be resolved inside a [io.github.subjekt.core.definition.Context].
 */
sealed class ResolvableSymbol

/**
 * Represents a [io.github.subjekt.core.Parameter] symbol.
 */
data class ParameterSymbol(
    /**
     * Identifier of the parameter.
     */
    val id: String,
) : ResolvableSymbol()

/**
 * Represents a [io.github.subjekt.core.Macro] symbol.
 */
data class MacroSymbol(
    /**
     * Identifier of the macro.
     */
    val id: String,
    /**
     * Number of arguments of the macro.
     */
    val nArgs: Int,
) : ResolvableSymbol()

/**
 * Represents a [io.github.subjekt.core.Macro] symbol qualified with a [io.github.subjekt.core.Module].
 */
data class QualifiedMacroSymbol(
    /**
     * Identifier of the module.
     */
    val module: String,
    /**
     * Identifier of the macro.
     */
    val id: String,
    /**
     * Number of arguments of the macro.
     */
    val nArgs: Int,
) : ResolvableSymbol()
