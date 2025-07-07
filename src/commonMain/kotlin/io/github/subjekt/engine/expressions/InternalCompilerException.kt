package io.github.subjekt.engine.expressions

import io.github.subjekt.engine.expressions.ir.IrNode

/**
 * Exception thrown when a symbol cannot be resolved.
 */
class InternalCompilerException(
    /**
     * Node that caused the exception.
     */
    val node: IrNode?,
    /**
     * Message describing the error.
     */
    override val message: String?,
) : Exception()
