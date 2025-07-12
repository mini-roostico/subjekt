/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions.visitors.ir.impl

import io.github.subjekt.engine.expressions.ParameterSymbol
import io.github.subjekt.engine.expressions.ResolvableSymbol
import io.github.subjekt.engine.expressions.SliceSymbol
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrEndOfSlice
import io.github.subjekt.engine.expressions.ir.IrIdentifier
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrTree
import io.github.subjekt.engine.expressions.toCallSymbol
import io.github.subjekt.engine.expressions.toParameterSymbol
import io.github.subjekt.engine.expressions.toQualifiedCallSymbol
import io.github.subjekt.engine.expressions.visitors.debug.LogVisitor

/**
 * Visitor for the intermediate representation of an expression
 * (a tree of [io.github.subjekt.engine.expressions.ir.IrNode]s). It finds the symbols used in the expression.
 */
private class ExpressionSymbolResolveVisitor : BaseExpressionVisitor<Set<ResolvableSymbol>>(emptySet()) {
    /**
     * Visitor for integer expressions. It is used to resolve the start, end, and step of range slices.
     */
    private val intVisitor: IntegerExpressionVisitor = IntegerExpressionVisitor()

    private fun ResolvableSymbol.toSet(): Set<ResolvableSymbol> = setOf(this)

    override fun visitBinaryOperation(node: IrBinaryOperation): Set<ResolvableSymbol> =
        visit(node.left) + visit(node.right)

    override fun visitCall(node: IrCall): Set<ResolvableSymbol> =
        node.arguments.flatMap { visit(it) }.toSet() + node.toCallSymbol()

    override fun visitParameter(node: IrIdentifier): Set<ResolvableSymbol> = setOf(node.toParameterSymbol())

    override fun visitDotCall(node: IrDotCall): Set<ResolvableSymbol> =
        node.arguments.flatMap { visit(it) }.toSet() + node.toQualifiedCallSymbol()

    override fun visitRangeSlice(node: IrRangeSlice): Set<ResolvableSymbol> =
        SliceSymbol(
            ParameterSymbol(node.identifier),
            intVisitor.visit(node.start),
            if (node.end is IrEndOfSlice) null else intVisitor.visit(node.end),
            intVisitor.visit(node.step),
        ).also { node.symbol = it }.toSet()

    override fun visitSingleSlice(node: IrSingleSlice): Set<ResolvableSymbol> = visit(node.indexExpression)
}

/**
 * Extracts all the [ResolvableSymbol] inside the expression's [IrTree].
 */
internal fun IrTree.resolveSymbols(log: Boolean = false): Set<ResolvableSymbol> =
    with(LogVisitor(log)) {
        visit(this@resolveSymbols)
        ExpressionSymbolResolveVisitor().visit(this@resolveSymbols)
    }
