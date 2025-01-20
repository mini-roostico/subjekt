/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions

import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.Symbol
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.core.definition.DefinedParameter

/**
 * Represents a symbol that can be resolved inside a [io.github.subjekt.core.definition.Context].
 */
sealed class ResolvableSymbol {
    /**
     * Resolves this [ResolvableSymbol] to the corresponding [Symbol] inside the given [symbolTable]. Can throw a
     * [SymbolNotFoundException] if the symbol is not found.
     */
    fun resolveToSymbol(symbolTable: SymbolTable): Symbol {
        symbolTable
        TODO()
    }
}

sealed class CallableSymbol : ResolvableSymbol() {
    /**
     * Identifier of the callable.
     */
    abstract val callableId: String

    /**
     * Number of arguments of the callable.
     */
    abstract val nArgs: Int

    /**
     * Resolves the symbol to a [io.github.subjekt.core.definition.DefinedMacro] using the given [Context].
     */
    fun resolveDefinedMacro(context: Context): DefinedMacro? = context.lookupMacro(callableId, nArgs)

    /**
     * Resolves the symbol to a [Function1] using the given [Context].
     */
    fun resolveFunction(context: Context): SubjektFunction? = context.lookupFunction(callableId)
}

/**
 * Obtains the [CallSymbol] associated to the [IrNode.IrCall].
 */
fun IrNode.IrCall.toCallSymbol(): CallSymbol = CallSymbol(identifier, arguments.size)

/**
 * Obtains the [QualifiedCallSymbol] associated to the [IrNode.IrDotCall].
 */
fun IrNode.IrDotCall.toQualifiedCallSymbol(): QualifiedCallSymbol =
    QualifiedCallSymbol(moduleId, callId, arguments.size)

/**
 * Obtains the [ParameterSymbol] associated to the [IrNode.IrParameter].
 */
fun IrNode.IrParameter.toParameterSymbol(): ParameterSymbol = ParameterSymbol(identifier)

/**
 * Represents a [io.github.subjekt.core.Parameter] symbol.
 */
data class ParameterSymbol(
    /**
     * Identifier of the parameter.
     */
    val id: String,
) : ResolvableSymbol() {
    /**
     * Resolves the symbol to a [io.github.subjekt.core.definition.DefinedParameter] using the given [Context].
     */
    fun resolveDefinedParameter(context: Context): DefinedParameter =
        context.lookupParameter(id) ?: throw SymbolNotFoundException(this)
}

/**
 * Represents a [io.github.subjekt.core.Macro] or [Function1] symbol.
 */
data class CallSymbol(
    override val callableId: String,
    override val nArgs: Int,
) : CallableSymbol()

/**
 * Represents a [io.github.subjekt.core.Macro] or [Function1] symbol qualified with a [io.github.subjekt.core.Module].
 */
data class QualifiedCallSymbol(
    /**
     * Identifier of the module.
     */
    val module: String,
    /**
     * Identifier of the macro.
     */
    val id: String,
    override val nArgs: Int,
) : CallableSymbol() {
    override val callableId: String
        get() = "$module.$id"
}
