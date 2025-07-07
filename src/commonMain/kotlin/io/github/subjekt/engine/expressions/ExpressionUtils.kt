package io.github.subjekt.engine.expressions

import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.Type
import kotlin.math.round

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
        type: Type,
        visitMethod: (IrNode) -> String,
    ): String {
        if (type == Type.UNDEFINED) {
            throw UnsupportedOperationException(
                "Cannot resolve binary operation with undefined type for operator $operator.",
            )
        }
        val leftStr = visitMethod(leftNode)
        val rightStr = visitMethod(rightNode)
        if (operator == BinaryOperator.CONCAT) return "$leftStr$rightStr"

        return when (type) {
            Type.STRING -> {
                if (operator == BinaryOperator.PLUS) {
                    // If the type is STRING and the operator is PLUS, we perform string concatenation
                    "$leftStr$rightStr"
                } else {
                    throw UnsupportedOperationException(
                        "Unsupported operation for type STRING with operator $operator",
                    )
                }
            }

            else -> {
                leftStr.applyNumberOperation(rightStr, operator, type)
            }
        }
    }

    private fun String.applyNumberOperation(
        right: String,
        operator: BinaryOperator,
        type: Type,
    ): String =
        when (type) {
            Type.INTEGER -> {
                val leftInt = this.toIntOrNull()
                val rightInt = right.toIntOrNull()
                leftInt.binaryOp(rightInt, operator)
            }
            Type.FLOAT -> {
                val leftFloat = this.toDoubleOrNull()
                val rightFloat = right.toDoubleOrNull()
                leftFloat.binaryOp(rightFloat, operator)
            }
            Type.NUMBER -> {
                val leftNumber = this.toIntOrNull() ?: this.toDoubleOrNull()
                val rightNumber = right.toIntOrNull() ?: right.toDoubleOrNull()

                if (leftNumber is Double || rightNumber is Double) {
                    leftNumber?.toDouble().binaryOp(rightNumber?.toDouble(), operator)
                } else {
                    leftNumber?.toInt().binaryOp(rightNumber?.toInt(), operator)
                }
            }
            else -> InternalCompilerException(null, "Unexpected else case in applyNumberOperation: $type")
        }?.toString() ?: throw IllegalArgumentException(
            "Cannot apply operator $operator on values '$this' and '$right' of type $type.",
        )

    private inline fun <reified T : Number> T?.binaryOp(
        right: T?,
        operator: BinaryOperator,
    ): String? =
        if (this == null || right == null) {
            null
        } else {
            when (operator) {
                BinaryOperator.PLUS -> (this.toDouble() + right.toDouble())
                BinaryOperator.MINUS -> (this.toDouble() - right.toDouble())
                BinaryOperator.MULTIPLY -> (this.toDouble() * right.toDouble())
                BinaryOperator.DIVIDE -> (this.toDouble() / right.toDouble())
                BinaryOperator.MODULO -> (this.toDouble() % right.toDouble())
                BinaryOperator.CONCAT -> "$this$right"
            }.let {
                if (T::class == Int::class && operator != BinaryOperator.CONCAT) {
                    round(it as Double).toInt().toString()
                } else {
                    it.toString()
                }
            }
        }
}
