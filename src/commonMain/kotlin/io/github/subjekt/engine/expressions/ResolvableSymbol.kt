/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions

import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.Symbol
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.core.definition.DefinedParameter
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.IrParameter
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.slices.SliceEngine.view

/**
 * Represents a symbol that can be resolved inside a [io.github.subjekt.core.definition.Context].
 */
sealed class ResolvableSymbol {
    /**
     * Resolves this [ResolvableSymbol] to the corresponding [Symbol] inside the given [symbolTable]. Can throw a
     * [SymbolNotFoundException] if the symbol is not found.
     */
    fun resolveToSymbol(symbolTable: SymbolTable): Symbol =
        when (this) {
            is ParameterSymbol -> symbolTable.resolveParameter(this.id) ?: throw SymbolNotFoundException(this)
            is CallableSymbol ->
                symbolTable.resolveMacro(this.callableId, this.nArgs)
                    ?: symbolTable.resolveFunction(this.callableId) ?: throw SymbolNotFoundException(this)
            is SliceSymbol ->
                view(
                    symbolTable.resolveParameter(parameter.id)
                        ?: throw SymbolNotFoundException(parameter),
                )
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
fun IrCall.toCallSymbol(): CallSymbol = CallSymbol(identifier, arguments.size)

/**
 * Obtains the [QualifiedCallSymbol] associated to the [IrNode.IrDotCall].
 */
fun IrDotCall.toQualifiedCallSymbol(): QualifiedCallSymbol = QualifiedCallSymbol(receiver, callId, arguments.size)

/**
 * Obtains the [ParameterSymbol] associated to the [IrNode.IrParameter].
 */
fun IrParameter.toParameterSymbol(): ParameterSymbol = ParameterSymbol(identifier)

fun IrSingleSlice.toParameterSymbol(): ParameterSymbol = ParameterSymbol(identifier)

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
    val receiver: IrNode,
    /**
     * Identifier of the macro.
     */
    val id: String,
    override val nArgs: Int,
) : CallableSymbol() {
    override val callableId: String
        get() = id
}

data class SliceSymbol(
    /**
     * Parameter symbol associated with the slice.
     */
    val parameter: ParameterSymbol,
    val startIndex: Int = 0,
    val endIndex: Int? = null,
    val stepIndex: Int = 1,
) : ResolvableSymbol() {
    /**
     * Identifier of the slice.
     */
    val identifier: String = "slice_${parameter.id}_$startIndex:${endIndex ?: ""}:$stepIndex"
}

/**
 * Converts an [IrRangeSlice] to a [ParameterSymbol].
 *
 * @throws InternalCompilerException if the slice has no symbol defined, which should not happen and should be
 * considered a bug.
 */
fun IrRangeSlice.toParameterSymbol(): ParameterSymbol =
    symbol?.run { ParameterSymbol(this.identifier) } ?: throw InternalCompilerException(
        this,
        "Slice symbol is not defined. This should not happen, please report this issue.",
    )
