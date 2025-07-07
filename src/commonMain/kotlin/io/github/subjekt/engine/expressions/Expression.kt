/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions

import io.github.subjekt.core.RawExpression
import io.github.subjekt.core.definition.Context
import io.github.subjekt.engine.expressions.ir.IrTree
import io.github.subjekt.engine.expressions.visitors.debug.LogVisitor
import io.github.subjekt.engine.expressions.visitors.ir.impl.ExpressionVisitor
import io.github.subjekt.engine.expressions.visitors.ir.impl.TypeVisitor
import io.github.subjekt.engine.expressions.visitors.ir.impl.resolveSymbols
import io.github.subjekt.engine.expressions.visitors.parseToIr

/**
 * Represents an expression in the Subjekt language. Differently from a [RawExpression], an [Expression] is a parsed and
 * can be resolved to a string result.
 */
class Expression(
    /**
     * Source code of the expression.
     */
    val source: String,
) {
    private val ir: IrTree by lazy {
        parseToIr()
    }

    /**
     * List of symbols used in the expression.
     */
    val symbols: Set<ResolvableSymbol> by lazy {
        ir.resolveSymbols()
    }

    /**
     * Resolves the expression to a string result using the given [Context].
     */
    @Throws(SymbolNotFoundException::class)
    fun resolve(
        context: Context,
        log: Boolean = false,
    ): String =
        TypeVisitor(context).visit(ir).run {
            with(LogVisitor(log)) {
                visit(ir)
                ExpressionVisitor(context).visit(ir)
            }
        }

    companion object {
        /**
         * Parses a [RawExpression] to an [Expression].
         */
        internal fun RawExpression.toExpression(): Expression = Expression(this.source)
    }
}
