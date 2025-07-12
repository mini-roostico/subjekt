package io.github.subjekt.engine.expressions.ir

import io.github.subjekt.engine.expressions.SliceSymbol

/**
 * Represents a node of the IR tree that has an identifier.
 */
sealed class IrSymbol(
    /**
     * Identifier of the node.
     */
    open val identifier: String,
    override val line: Int,
) : IrBasicNode(line)

/**
 * Represents an identifier node (e.g. `${{ name }}`).
 */
data class IrIdentifier(
    /**
     * Identifier value.
     */
    override val identifier: String,
    override val line: Int,
) : IrSymbol(identifier, line)

/**
 * Represents a call node (e.g. `${{ name() }}`).
 */
data class IrCall(
    /**
     * Identifier of the call.
     */
    override val identifier: String,
    /**
     * Argument expressions of the call.
     */
    val arguments: List<IrNode>,
    override val line: Int,
) : IrSymbol(identifier, line)

/**
 * Represents a dot call node (e.g. `${{ std.name() }}`).
 */
data class IrDotCall(
    /**
     * Identifier of the module (e.g. `std`).
     */
    val receiver: IrNode,
    /**
     * Identifier of the call (e.g. `name`).
     */
    val callId: String,
    /**
     * Argument expressions of the call.
     */
    val arguments: List<IrNode>,
    override val line: Int,
) : IrSymbol(callId, line)

data class IrSingleSlice(
    override val identifier: String,
    /**
     * Index of the slice.
     */
    val indexExpression: IrNode,
    override val line: Int = -1,
) : IrSymbol(identifier, line)

/**
 * Represents a slice operation in the Subjekt IR.
 */
data class IrRangeSlice(
    override val identifier: String,
    /**
     * Start expression of the slice.
     */
    val start: IrBasicNode = IrIntegerLiteral(0, -1),
    /**
     * End expression of the slice.
     */
    val end: IrBasicNode = IrEndOfSlice(-1),
    /**
     * Step expression of the slice.
     */
    val step: IrBasicNode = IrIntegerLiteral(1, -1),
    /**
     * Symbol representing the slice.
     */
    var symbol: SliceSymbol? = null,
    override val line: Int = -1,
) : IrSymbol(identifier, line)

/**
 * Special atomic node needed to mark a slice with no end index.
 */
data class IrEndOfSlice(
    override val line: Int = -1,
) : IrBasicNode(line)
