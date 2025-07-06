package io.github.subjekt.compiler.expressions.ir

/**
 * Represents a node in the IR tree that can be resolved to multiple values (not still supported).
 */
sealed class IrResolvableNode(
    /**
     * Line in the source code where this node is located.
     */
    override val line: Int,
) : IrNode(line)
