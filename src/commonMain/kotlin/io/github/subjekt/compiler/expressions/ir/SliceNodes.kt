package io.github.subjekt.compiler.expressions.ir

sealed class IrRangeSlice(
    /**
     * Identifier of the slice (e.g. `name`).
     */
    open val identifier: String,
    /**
     * Line in the source code where this node is located.
     */
    override val line: Int = -1,
) : IrResolvableNode(line)

data class IrStartEndSlice(
    override val identifier: String,
    /**
     * Start expression of the slice.
     */
    val start: IrAtomicNode,
    /**
     * End expression of the slice.
     */
    val end: IrAtomicNode,
    override val line: Int = -1,
) : IrRangeSlice(identifier, line)

data class IrEndSlice(
    override val identifier: String,
    /**
     * End expression of the slice.
     */
    val end: IrAtomicNode,
    override val line: Int = -1,
) : IrRangeSlice(identifier, line)

data class IrStartSlice(
    override val identifier: String,
    /**
     * Start expression of the slice.
     */
    val start: IrAtomicNode,
    override val line: Int = -1,
) : IrRangeSlice(identifier, line)

data class IrCompleteSlice(
    override val identifier: String,
    /**
     * Start expression of the slice.
     */
    val start: IrAtomicNode = IrIntegerLiteral(0, -1),
    /**
     * End expression of the slice.
     */
    val end: IrAtomicNode = IrEndOfSlice(-1),
    /**
     * Step expression of the slice.
     */
    val step: IrAtomicNode,
    override val line: Int = -1,
) : IrRangeSlice(identifier, line)

/**
 * Special atomic node needed to mark a slice with no end index.
 */
data class IrEndOfSlice(
    override val line: Int = -1,
) : IrAtomicNode(line)
