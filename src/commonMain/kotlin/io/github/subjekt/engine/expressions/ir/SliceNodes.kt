package io.github.subjekt.engine.expressions.ir

import io.github.subjekt.engine.expressions.SliceSymbol

/**
 * Represents a slice operation in the Subjekt IR.
 */
data class IrRangeSlice(
    val identifier: String,
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
    val step: IrAtomicNode = IrIntegerLiteral(1, -1),
    /**
     * Symbol representing the slice.
     */
    var symbol: SliceSymbol? = null,
    override val line: Int = -1,
) : IrResolvableNode(line)

/**
 * Special atomic node needed to mark a slice with no end index.
 */
data class IrEndOfSlice(
    override val line: Int = -1,
) : IrAtomicNode(line)
