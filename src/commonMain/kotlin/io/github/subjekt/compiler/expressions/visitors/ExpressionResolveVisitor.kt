/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.visitors

import io.github.subjekt.compiler.expressions.CallableSymbol
import io.github.subjekt.compiler.expressions.call
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.expressions.toCallSymbol
import io.github.subjekt.compiler.expressions.toParameterSymbol
import io.github.subjekt.compiler.expressions.toQualifiedCallSymbol
import io.github.subjekt.core.definition.Context

/**
 * Visitor that resolves an [IrNode] to a string result, using the given [Context].
 */
internal class ExpressionResolveVisitor(
    /**
     * Context used to resolve symbols in the expression to visit.
     */
    private val context: Context,
) : ExpressionIrVisitor<String> {
    private fun callSymbol(
        symbol: CallableSymbol,
        arguments: List<String>,
    ): String =
        runCatching {
            val definedMacro = symbol.resolveMacro(context)
            definedMacro.call(arguments)
        }.fold({ it }) {
            val definedFunction = symbol.resolveFunction(context)
            definedFunction(arguments)
        }

    override fun visitCall(node: IrNode.IrCall): String {
        val symbol = node.toCallSymbol()
        return callSymbol(symbol, node.arguments.map { visit(it) })
    }

    override fun visitParameter(node: IrNode.IrParameter): String =
        node
            .toParameterSymbol()
            .resolve(context)
            .value
            .toString()

    override fun visitPlus(node: IrNode.IrExpressionPlus): String {
        val left = visit(node.left)
        val right = visit(node.right)
        return "$left$right"
    }

    override fun visitLiteral(node: IrNode.IrLiteral): String = node.value

    override fun visitDotCall(node: IrNode.IrDotCall): String {
        val symbol = node.toQualifiedCallSymbol()
        return callSymbol(symbol, node.arguments.map { visit(it) })
    }
}
