/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.visitors

import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.expressions.ir.IrNode.IrCall
import io.github.subjekt.compiler.expressions.ir.IrNode.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrNode.IrExpressionPlus
import io.github.subjekt.compiler.expressions.ir.IrNode.IrLiteral
import io.github.subjekt.compiler.expressions.ir.IrNode.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrNode.IrTree
import io.github.subjekt.utils.Utils.parsingFail

/**
 * Visitor for the intermediate representation of an expression (a tree of [IrNode]s).
 */
interface ExpressionIrVisitor<T> {
    /**
     * Visits a [IrCall] node.
     */
    fun visitCall(node: IrCall): T

    /**
     * Visits a [IrParameter] node.
     */
    fun visitParameter(node: IrParameter): T

    /**
     * Visits a [IrExpressionPlus] node.
     */
    fun visitPlus(node: IrExpressionPlus): T

    /**
     * Visits a [IrLiteral] node.
     */
    fun visitLiteral(node: IrLiteral): T

    /**
     * Visits a [IrDotCall] node.
     */
    fun visitDotCall(node: IrDotCall): T

    /**
     * Visits a [IrNode], calling the appropriate visit method based on the type of the node.
     */
    fun visit(node: IrNode): T =
        when (node) {
            is IrTree -> visit(node.node)
            is IrCall -> visitCall(node)
            is IrParameter -> visitParameter(node)
            is IrExpressionPlus -> visitPlus(node)
            is IrLiteral -> visitLiteral(node)
            is IrDotCall -> visitDotCall(node)
            is IrNode.Error -> parsingFail { "Error node found in the IR tree" }
        }
}
