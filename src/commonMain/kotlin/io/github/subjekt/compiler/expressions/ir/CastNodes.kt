package io.github.subjekt.compiler.expressions.ir

enum class IrNativeType {
    INTEGER,
    FLOAT,
    STRING,
}

data class IrCast(
    var value: IrNode?,
    val targetType: IrNativeType,
    override val line: Int = -1,
) : IrAtomicNode(line)
