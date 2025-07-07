package io.github.subjekt.engine.expressions.visitors.ir.impl

import io.github.subjekt.engine.expressions.ExpressionUtils
import io.github.subjekt.engine.expressions.InternalCompilerException
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.engine.expressions.ir.Type
import io.github.subjekt.engine.expressions.ir.UnaryOperator

/**
 * Visitor for evaluating integer expressions in the intermediate representation (IR).
 */
class IntegerExpressionVisitor :
    BaseExpressionVisitor<Int>(
        Int.MIN_VALUE,
    ) {
    override fun visitIntegerLiteral(node: IrIntegerLiteral): Int = node.value

    override fun visitFloatLiteral(node: IrFloatLiteral): Int = node.value.toInt()

    override fun visitStringLiteral(node: IrStringLiteral): Int = node.value.length

    override fun visitCast(node: IrCast): Int =
        node.value?.accept(this) ?: throw InternalCompilerException(
            node,
            "Missing value in cast expression: ${node.type}",
        )

    override fun visitBinaryOperation(node: IrBinaryOperation): Int =
        ExpressionUtils
            .resolveBinaryOperation(
                leftNode = node.left,
                rightNode = node.right,
                operator = node.operator,
                type = Type.INTEGER,
                visitMethod = { visit(it).toString() },
            ).toInt()

    override fun visitUnaryOperation(node: IrUnaryOperation): Int =
        if (node.operator == UnaryOperator.MINUS) -visit(node.operand) else visit(node.operand)

    override fun visit(node: IrNode): Int =
        when (node) {
            is IrIntegerLiteral,
            is IrFloatLiteral,
            is IrStringLiteral,
            is IrCast,
            is IrBinaryOperation,
            is IrUnaryOperation,
            ->
                super.visit(
                    node,
                )

            else -> throw IllegalArgumentException(
                "Unsupported node type in integer expression:" +
                    " ${node::class.simpleName}",
            )
        }
}
