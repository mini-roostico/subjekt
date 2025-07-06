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
 * Exception thrown when a symbol cannot be resolved.
 */
class SymbolNotFoundException(
    /**
     * Symbol that cannot be resolved.
     */
    private val symbol: ResolvableSymbol,
) : Exception() {
    override val message: String?
        get() =
            when (symbol) {
                is CallSymbol ->
                    "Called '${symbol.callableId}' symbol with ${symbol.nArgs} arguments," +
                        " but '${symbol.callableId}/${symbol.nArgs}' cannot be resolved."
                is ParameterSymbol -> "Parameter '${symbol.id}' cannot be resolved."
                is QualifiedCallSymbol ->
                    "Called '${symbol.receiver}.${symbol.callableId}' symbol with ${symbol.nArgs} arguments," +
                        " but '${symbol.receiver}.${symbol.callableId}/${symbol.nArgs}' cannot be resolved."
                is SliceSymbol ->
                    "Slice on parameter '${symbol.identifier}' cannot be resolved."
            }
}
