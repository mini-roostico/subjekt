package io.github.subjekt.engine.expressions

import io.github.subjekt.core.value.Type
import io.github.subjekt.core.value.Value
import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.IrNode

object ExpressionUtils {
    /**
     * Resolves a binary operation between two [IrNode]s based on the provided [BinaryOperator] and [Type].
     *
     * @param leftNode The left operand of the binary operation.
     * @param rightNode The right operand of the binary operation.
     * @param operator The binary operator to apply.
     * @param type The type of the operation, which determines how the operation is resolved.
     * @param visitMethod A function that converts an [IrNode] to a string representation.
     * @return A string representation of the result of the binary operation.
     */
    internal fun resolveBinaryOperation(
        leftNode: IrNode,
        rightNode: IrNode,
        operator: BinaryOperator,
        visitMethod: (IrNode) -> Value,
    ): Value {
        val left = visitMethod(leftNode)
        val right = visitMethod(rightNode)

        return when (operator) {
            BinaryOperator.PLUS -> left + right
            BinaryOperator.MINUS -> left - right
            BinaryOperator.MULTIPLY -> left * right
            BinaryOperator.DIVIDE -> left / right
            BinaryOperator.MODULO -> left % right
            BinaryOperator.EQ -> left eq right
            BinaryOperator.NE -> left ne right
            BinaryOperator.LT -> left lt right
            BinaryOperator.LE -> left le right
            BinaryOperator.GT -> left gt right
            BinaryOperator.GE -> left ge right
            BinaryOperator.AND -> left and right
            BinaryOperator.OR -> left or right
            BinaryOperator.CONCAT -> left concat right
        }
    }
}
