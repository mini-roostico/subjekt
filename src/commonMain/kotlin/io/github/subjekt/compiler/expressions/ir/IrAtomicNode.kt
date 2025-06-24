package io.github.subjekt.compiler.expressions.ir

sealed class IrAtomicNode(
    /**
     * Line in the source code where this node is located.
     */
    override val line: Int,
) : IrNode(line)
