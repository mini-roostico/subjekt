package io.github.subjekt.engine.expressions.ir

import io.github.subjekt.core.value.Type

/**
 * Represents a native type in the Subjekt IR.
 */
enum class IrNativeType {
    INTEGER,
    FLOAT,
    STRING,
    ;

    fun toType(): Type =
        when (this) {
            INTEGER -> Type.INTEGER
            FLOAT -> Type.FLOAT
            STRING -> Type.STRING
        }
}

/**
 * Represents a cast operation in the Subjekt IR. [value] can be null if the cast is not yet resolved or if it is a
 * placeholder.
 */
data class IrCast(
    var value: IrNode?,
    val targetType: IrNativeType,
    override val line: Int = -1,
) : IrBasicNode(line)
