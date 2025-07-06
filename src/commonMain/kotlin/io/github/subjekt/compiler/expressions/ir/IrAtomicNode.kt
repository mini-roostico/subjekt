package io.github.subjekt.compiler.expressions.ir

sealed class IrAtomicNode(
    /**
     * Line in the source code where this node is located.
     */
    override val line: Int,
) : IrNode(line)

enum class UnaryOperator {
    MINUS,
    PLUS,
}

data class IrUnaryOperation(
    /**
     * The operator of the unary operation (e.g. `!`, `-`).
     */
    val operator: UnaryOperator,
    /**
     * The operand of the unary operation.
     */
    val operand: IrNode,
    override val line: Int = -1,
) : IrAtomicNode(line)
