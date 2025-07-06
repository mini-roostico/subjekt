package io.github.subjekt.compiler.expressions.ir

/**
 * Represents a literal value in the Subjekt IR.
 */
sealed class IrLiteral(
    override val line: Int,
) : IrAtomicNode(line)

/**
 * Represents a string literal node (e.g. `${{ "Hello" }}` or `${{ 'Hello' }}`).
 */
data class IrStringLiteral(
    /**
     * Literal value (without quotes).
     */
    val value: String,
    override val line: Int,
) : IrLiteral(line)

/**
 * Represents an integer literal node (e.g. `${{ 42 }}`).
 */
data class IrIntegerLiteral(
    /**
     * Integer literal value.
     */
    val value: Int,
    override val line: Int,
) : IrLiteral(line)

/**
 * Represents a float literal node (e.g. `${{ 3.14 }}`).
 */
data class IrFloatLiteral(
    /**
     * Float literal value.
     */
    val value: Double,
    override val line: Int,
) : IrLiteral(line)
