package io.github.subjekt.engine.expressions.visitors.ir.impl

import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrEndOfSlice
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIdentifier
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.engine.expressions.visitors.ir.IrVisitor

/**
 * Base implementation of [IrVisitor] that provides default behavior for visiting.
 */
abstract class BaseExpressionVisitor<T>(
    /**
     * Default value to return when visiting nodes.
     */
    private val default: T,
) : IrVisitor<T> {
    override fun visitBinaryOperation(node: IrBinaryOperation): T {
        visit(node.left)
        visit(node.right)
        return default
    }

    override fun visitCast(node: IrCast): T = node.value?.accept(this) ?: default

    override fun visitEndOfSlice(node: IrEndOfSlice): T = default

    override fun visitCall(node: IrCall): T {
        node.arguments.forEach { visit(it) }
        return default
    }

    override fun visitParameter(node: IrIdentifier): T = default

    override fun visitDotCall(node: IrDotCall): T {
        node.arguments.forEach { visit(it) }
        return default
    }

    override fun visitSingleSlice(node: IrSingleSlice): T = visit(node.indexExpression)

    override fun visitFloatLiteral(node: IrFloatLiteral): T = default

    override fun visitIntegerLiteral(node: IrIntegerLiteral): T = default

    override fun visitStringLiteral(node: IrStringLiteral): T = default

    override fun visitRangeSlice(node: IrRangeSlice): T {
        visit(node.start)
        visit(node.end)
        visit(node.step)
        return default
    }

    override fun visitUnaryOperation(node: IrUnaryOperation): T = visit(node.operand)
}
