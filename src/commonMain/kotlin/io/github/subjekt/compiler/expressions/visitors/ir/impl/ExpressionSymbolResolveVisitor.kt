/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.visitors.ir.impl

import io.github.subjekt.compiler.expressions.CallSymbol
import io.github.subjekt.compiler.expressions.ParameterSymbol
import io.github.subjekt.compiler.expressions.QualifiedCallSymbol
import io.github.subjekt.compiler.expressions.ResolvableSymbol
import io.github.subjekt.compiler.expressions.ir.IrBinaryOperation
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrCompleteSlice
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrStartEndSlice
import io.github.subjekt.compiler.expressions.ir.IrTree
import io.github.subjekt.compiler.expressions.visitors.ir.impl.base.BaseExpressionVisitor

/**
 * Visitor for the intermediate representation of an expression (a tree of [IrNode]s). It finds the symbols used in
 * the expression.
 */
private class ExpressionSymbolResolveVisitor : BaseExpressionVisitor<Set<ResolvableSymbol>>(emptySet()) {
    override fun visitBinaryOperation(node: IrBinaryOperation): Set<ResolvableSymbol> =
        visit(node.left) + visit(node.right)

    override fun visitCall(node: IrCall): Set<ResolvableSymbol> =
        node.arguments.flatMap { visit(it) }.toSet() +
            CallSymbol(node.identifier, node.arguments.size)

    override fun visitParameter(node: IrParameter): Set<ResolvableSymbol> = setOf(ParameterSymbol(node.identifier))

    override fun visitDotCall(node: IrDotCall): Set<ResolvableSymbol> =
        node.arguments.flatMap { visit(it) }.toSet() +
            QualifiedCallSymbol(node.moduleId, node.callId, node.arguments.size)

    override fun visitCompleteSlice(node: IrCompleteSlice): Set<ResolvableSymbol> =
        visit(node.start) + visit(node.end) + visit(node.step)

    override fun visitStartEndSlice(node: IrStartEndSlice): Set<ResolvableSymbol> = visit(node.start) + visit(node.end)
}

/**
 * Extracts all the [ResolvableSymbol] inside the expression's [IrTree].
 */
internal fun IrTree.resolveSymbols(): Set<ResolvableSymbol> = ExpressionSymbolResolveVisitor().visit(this)
