package io.github.subjekt.compiler.expressions.ir

enum class BinaryOperator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    CONCAT,
}

data class IrBinaryOperation(
    /**
     * Left operand.
     */
    val left: IrNode,
    /**
     * Right operand.
     */
    val right: IrNode,
    /**
     * Operator of the binary operation (e.g. `+`, `-`, `*`, `/`).
     */
    val operator: BinaryOperator,
    override val line: Int,
) : IrAtomicNode(line)
