/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.permutations

import io.github.subjekt.compiler.expressions.Expression.Companion.toExpression
import io.github.subjekt.compiler.expressions.ParameterSymbol
import io.github.subjekt.compiler.expressions.ResolvableSymbol
import io.github.subjekt.core.Macro
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.Subject
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.Symbol
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.core.definition.DefinedParameter

/**
 * Extracts all the contexts needed to resolve every permutation of the symbols' values contained in the resolvables of
 * this subject.
 */
fun Subject.requestNeededContexts(): List<Context> {
    val resolvableSymbols = resolvables.flatMap { it.value.expressions.flatMap { it.toExpression().symbols } }
    val parameters = mutableSetOf<List<DefinedParameter>>()
    val macros = mutableSetOf<List<DefinedMacro>>()
    val functions = mutableSetOf<SubjektFunction>()
    val symbols = resolvableSymbols.map { it.resolveToSymbol(symbolTable) }
    symbols.forEach {
        it.populateDefinedSymbols(symbolTable, parameters, macros, functions)
    }
    return symbolTable.contextPermutationsOutOf(parameters, macros, functions)
}

/**
 * Populates [parameters], [macros], and [functions] with the defined symbols contained this symbol and its children.
 */
internal fun Symbol.populateDefinedSymbols(
    symbolTable: SymbolTable,
    parameters: MutableSet<List<DefinedParameter>>,
    macros: MutableSet<List<DefinedMacro>>,
    functions: MutableSet<SubjektFunction>,
) {
    when (this) {
        is Parameter -> parameters += toDefinedParameters()
        is Macro -> {
            macros += toDefinedMacros()
            extractNeededSymbols()
                .map {
                    it.resolveToSymbol(symbolTable)
                }.forEach { it.populateDefinedSymbols(symbolTable, parameters, macros, functions) }
        }
        is SubjektFunction -> functions += this
    }
}

/**
 * Extracts all the values from this parameter and creates a set of [DefinedParameter]s with those values.
 */
internal fun Parameter.toDefinedParameters(): List<DefinedParameter> =
    values.map {
        DefinedParameter(id, it, this)
    }

/**
 * Extracts all the values from this macro and creates a set of [DefinedMacro]s with those values.
 */
internal fun Macro.toDefinedMacros(): List<DefinedMacro> =
    resolvables
        .map {
            DefinedMacro(id, argumentsIdentifiers, it)
        }

/**
 * Extracts all the [ResolvableSymbol]s used inside the bodies of this macro and returns them as a set.
 */
internal fun Macro.extractNeededSymbols(): Set<ResolvableSymbol> =
    resolvables
        .flatMap {
            it.expressions.flatMap { it.toExpression().symbols }
        }.filterNot { it is ParameterSymbol && it.id in argumentsIdentifiers }
        .toSet()
