/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.visitors

import io.github.subjekt.compiler.expressions.CallSymbol
import io.github.subjekt.compiler.expressions.ParameterSymbol
import io.github.subjekt.compiler.expressions.QualifiedCallSymbol
import io.github.subjekt.compiler.expressions.ResolvableSymbol
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.expressions.ir.IrNode.IrTree

/**
 * Visitor for the intermediate representation of an expression (a tree of [IrNode]s). It finds the symbols used in
 * the expression.
 */
private class ExpressionSymbolResolveVisitor : ExpressionIrVisitor<Set<ResolvableSymbol>> {
    override fun visitCall(node: IrNode.IrCall): Set<ResolvableSymbol> =
        node.arguments.flatMap { visit(it) }.toSet() + CallSymbol(node.identifier, node.arguments.size)

    override fun visitParameter(node: IrNode.IrParameter): Set<ResolvableSymbol> =
        setOf(ParameterSymbol(node.identifier))

    override fun visitPlus(node: IrNode.IrExpressionPlus): Set<ResolvableSymbol> = visit(node.left) + visit(node.right)

    override fun visitLiteral(node: IrNode.IrLiteral): Set<ResolvableSymbol> = emptySet()

    override fun visitDotCall(node: IrNode.IrDotCall): Set<ResolvableSymbol> =
        node.arguments.flatMap { visit(it) }.toSet() +
            QualifiedCallSymbol(node.moduleId, node.callId, node.arguments.size)
}

/**
 * Extracts all the [ResolvableSymbol] inside the expression's [IrTree].
 */
internal fun IrTree.resolveSymbols(): Set<ResolvableSymbol> = ExpressionSymbolResolveVisitor().visit(this)
