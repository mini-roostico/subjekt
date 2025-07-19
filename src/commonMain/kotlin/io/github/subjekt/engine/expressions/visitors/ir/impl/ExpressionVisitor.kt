package io.github.subjekt.engine.expressions.visitors.ir.impl

import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.value.Value
import io.github.subjekt.engine.expressions.ExpressionUtils
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIdentifier
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.engine.expressions.ir.UnaryOperator
import io.github.subjekt.engine.expressions.ir.utils.IrUtils.callSymbol
import io.github.subjekt.engine.expressions.toCallSymbol
import io.github.subjekt.engine.expressions.toParameterSymbol
import io.github.subjekt.engine.expressions.toQualifiedCallSymbol

/**
 * Visitor for the intermediate representation of an expression
 * (a tree of [io.github.subjekt.engine.expressions.ir.IrNode]s). It evaluates the expression
 * and returns a string representation of the result.
 */
class ExpressionVisitor(
    /**
     * The context in which the expression is evaluated.
     */
    val context: Context,
) : BaseExpressionVisitor<Value>(Value.ofString("")) {
    override fun visitCall(node: IrCall): Value {
        val symbol = node.toCallSymbol()
        return context.callSymbol(symbol, node.arguments.map { visit(it) })
    }

    override fun visitParameter(node: IrIdentifier): Value =
        node
            .toParameterSymbol()
            .resolveDefinedParameter(context)
            .value

    override fun visitDotCall(node: IrDotCall): Value {
        val symbol = node.toQualifiedCallSymbol()
        return context.callSymbol(symbol, node.arguments.map { visit(it) })
    }

    override fun visitSingleSlice(node: IrSingleSlice): Value {
        val originalParameter =
            context.originalSymbolTable?.resolveParameter(node.identifier)
                ?: throw IllegalArgumentException(
                    "Performing slice on a DefinedParameter '${node.identifier}' " +
                        "with no original parameter defined. Are you defining a Parameter programmatically?",
                )
        val index =
            visit(node.indexExpression).toIntOrNull() ?: throw IllegalArgumentException(
                "Expected integer index for slice on parameter '${node.identifier}', " +
                    "but got: ${node.indexExpression}",
            )
        return originalParameter.values.getOrNull(index) ?: throw IndexOutOfBoundsException(
            "Index $index out of bounds for parameter '${node.identifier}' with " +
                "${originalParameter.values.size} values.",
        )
    }

    override fun visitBinaryOperation(node: IrBinaryOperation): Value =
        ExpressionUtils.resolveBinaryOperation(
            leftNode = node.left,
            rightNode = node.right,
            operator = node.operator,
            visitMethod = { visit(it) },
        )

    override fun visitCast(node: IrCast): Value {
        val value =
            node.value?.accept(this) ?: throw IllegalArgumentException(
                "Cannot cast null value to ${node.targetType}.",
            )
        return value.cast(node.targetType.toType())
    }

    override fun visitUnaryOperation(node: IrUnaryOperation): Value =
        when (node.operator) {
            UnaryOperator.MINUS -> -visit(node.operand)
            UnaryOperator.PLUS -> visit(node.operand)
            UnaryOperator.NOT -> !visit(node.operand)
        }

    override fun visitRangeSlice(node: IrRangeSlice): Value =
        node.toParameterSymbol().resolveDefinedParameter(context).value

    override fun visitFloatLiteral(node: IrFloatLiteral): Value = Value.ofDouble(node.value)

    override fun visitIntegerLiteral(node: IrIntegerLiteral): Value = Value.ofInt(node.value)

    override fun visitStringLiteral(node: IrStringLiteral): Value = Value.ofString(node.value)
}
