package io.github.subjekt.compiler.expressions.ir

/**
 * Represents an atomic (simple) node in the IR tree.
 */
sealed class IrAtomicNode(
    /**
     * Line in the source code where this node is located.
     */
    override val line: Int,
) : IrNode(line)

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
) : IrAtomicNode(line)
