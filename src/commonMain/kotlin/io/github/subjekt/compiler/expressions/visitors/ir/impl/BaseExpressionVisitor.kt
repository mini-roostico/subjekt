package io.github.subjekt.compiler.expressions.visitors.ir.impl

import io.github.subjekt.compiler.expressions.ir.IrBinaryOperation
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrCast
import io.github.subjekt.compiler.expressions.ir.IrCompleteSlice
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrEndOfSlice
import io.github.subjekt.compiler.expressions.ir.IrEndSlice
import io.github.subjekt.compiler.expressions.ir.IrFloatLiteral
import io.github.subjekt.compiler.expressions.ir.IrIntegerLiteral
import io.github.subjekt.compiler.expressions.ir.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrSingleSlice
import io.github.subjekt.compiler.expressions.ir.IrStartEndSlice
import io.github.subjekt.compiler.expressions.ir.IrStartSlice
import io.github.subjekt.compiler.expressions.ir.IrStringLiteral
import io.github.subjekt.compiler.expressions.visitors.ir.IrVisitor

abstract class BaseExpressionVisitor<T>(
    private val default: T,
) : IrVisitor<T> {
    override fun visitBinaryOperation(node: IrBinaryOperation): T {
        visit(node.left)
        visit(node.right)
        return default
    }

    override fun visitCast(node: IrCast): T = visit(node.value)

    override fun visitEndOfSlice(node: IrEndOfSlice): T = default

    override fun visitCall(node: IrCall): T {
        node.arguments.forEach { visit(it) }
        return default
    }

    override fun visitParameter(node: IrParameter): T = default

    override fun visitDotCall(node: IrDotCall): T {
        node.arguments.forEach { visit(it) }
        return default
    }

    override fun visitSingleSlice(node: IrSingleSlice): T = visit(node.indexExpression)

    override fun visitFloatLiteral(node: IrFloatLiteral): T = default

    override fun visitIntegerLiteral(node: IrIntegerLiteral): T = default

    override fun visitStringLiteral(node: IrStringLiteral): T = default

    override fun visitCompleteSlice(node: IrCompleteSlice): T {
        visit(node.start)
        visit(node.end)
        visit(node.step)
        return default
    }

    override fun visitEndSlice(node: IrEndSlice): T = visit(node.end)

    override fun visitStartEndSlice(node: IrStartEndSlice): T {
        visit(node.start)
        visit(node.end)
        return default
    }

    override fun visitStartSlice(node: IrStartSlice): T = visit(node.start)
}
