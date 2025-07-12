package io.github.subjekt.engine.expressions.ir

/**
 * Represents a binary operation in the Subjekt language.
 */
enum class BinaryOperator {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULO,
    CONCAT,
}

/**
 * Represents a binary operation in the IR tree.
 */
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
) : IrBasicNode(line)

/**
 * Represents a unary operation in the Subjekt language.
 */
enum class UnaryOperator {
    MINUS,
    PLUS,
}

/**
 * Represents a unary operation in the IR tree.
 */
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
) : IrBasicNode(line)
