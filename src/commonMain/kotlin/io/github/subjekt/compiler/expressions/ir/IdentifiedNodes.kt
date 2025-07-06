package io.github.subjekt.compiler.expressions.ir

sealed class IrIdentified(
    open val identifier: String,
    override val line: Int,
) : IrAtomicNode(line)

/**
 * Represents an identifier node (e.g. `${{ name }}`).
 */
data class IrParameter(
    /**
     * Identifier value.
     */
    override val identifier: String,
    override val line: Int,
) : IrIdentified(identifier, line)

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
) : IrIdentified(identifier, line)

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
) : IrIdentified(callId, line)

data class IrSingleSlice(
    override val identifier: String,
    /**
     * Index of the slice.
     */
    val indexExpression: IrNode,
    override val line: Int = -1,
) : IrIdentified(identifier, line)
