package io.github.subjekt.compiler.expressions.visitors.ir.impl

import io.github.subjekt.compiler.expressions.ExpressionUtils
import io.github.subjekt.compiler.expressions.ir.IrBinaryOperation
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrCast
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrFloatLiteral
import io.github.subjekt.compiler.expressions.ir.IrIntegerLiteral
import io.github.subjekt.compiler.expressions.ir.IrNativeType
import io.github.subjekt.compiler.expressions.ir.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrRangeSlice
import io.github.subjekt.compiler.expressions.ir.IrSingleSlice
import io.github.subjekt.compiler.expressions.ir.IrStringLiteral
import io.github.subjekt.compiler.expressions.ir.IrUnaryOperation
import io.github.subjekt.compiler.expressions.ir.UnaryOperator
import io.github.subjekt.compiler.expressions.ir.utils.IrUtils.callSymbol
import io.github.subjekt.compiler.expressions.toCallSymbol
import io.github.subjekt.compiler.expressions.toParameterSymbol
import io.github.subjekt.compiler.expressions.toQualifiedCallSymbol
import io.github.subjekt.core.definition.Context

/**
 * Visitor for the intermediate representation of an expression
 * (a tree of [io.github.subjekt.compiler.expressions.ir.IrNode]s). It evaluates the expression
 * and returns a string representation of the result.
 */
class ExpressionVisitor(
    /**
     * The context in which the expression is evaluated.
     */
    val context: Context,
) : BaseExpressionVisitor<String>("") {
    override fun visitCall(node: IrCall): String {
        val symbol = node.toCallSymbol()
        return context.callSymbol(symbol, node.arguments.map { visit(it) })
    }

    override fun visitParameter(node: IrParameter): String =
        node
            .toParameterSymbol()
            .resolveDefinedParameter(context)
            .value

    override fun visitDotCall(node: IrDotCall): String {
        val symbol = node.toQualifiedCallSymbol()
        return context.callSymbol(symbol, node.arguments.map { visit(it) })
    }

    override fun visitSingleSlice(node: IrSingleSlice): String {
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

    override fun visitBinaryOperation(node: IrBinaryOperation): String =
        ExpressionUtils.resolveBinaryOperation(
            leftNode = node.left,
            rightNode = node.right,
            operator = node.operator,
            type = node.type,
            visitMethod = { visit(it) },
        )

    override fun visitCast(node: IrCast): String {
        val value =
            node.value?.accept(this) ?: throw IllegalArgumentException(
                "Cannot cast null value to ${node.targetType}.",
            )
        return when (node.targetType) {
            IrNativeType.INTEGER ->
                value.toIntOrNull()?.toString()
                    ?: throw IllegalArgumentException("Cannot cast '$value' to INTEGER.")

            IrNativeType.FLOAT ->
                value.toDoubleOrNull()?.toString()
                    ?: throw IllegalArgumentException("Cannot cast '$value' to FLOAT.")

            IrNativeType.STRING -> value
        }
    }

    override fun visitUnaryOperation(node: IrUnaryOperation): String =
        when (node.operator) {
            UnaryOperator.MINUS -> "-${visit(node.operand)}"
            UnaryOperator.PLUS -> visit(node.operand)
        }

    override fun visitRangeSlice(node: IrRangeSlice): String =
        node.toParameterSymbol().resolveDefinedParameter(context).value

    override fun visitFloatLiteral(node: IrFloatLiteral): String = node.value.toString()

    override fun visitIntegerLiteral(node: IrIntegerLiteral): String = node.value.toString()

    override fun visitStringLiteral(node: IrStringLiteral): String = node.value
}
