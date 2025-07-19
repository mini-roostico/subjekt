package io.github.subjekt.core.value

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@JsExport
@OptIn(ExperimentalJsExport::class)
data class BooleanValue(
    val value: Boolean,
) : Value(Type.BOOLEAN) {
    override fun toString(): String = value.toString()

    override fun cast(targetType: Type): Value =
        when (targetType) {
            Type.STRING -> StringValue(value.toString())
            Type.INTEGER -> IntValue(if (value) 1 else 0)
            Type.FLOAT -> FloatValue(if (value) 1.0 else 0.0)
            Type.BOOLEAN -> this
            else -> throw UnsupportedOperationException("Cannot cast boolean value $this to $targetType.")
        }

    override fun eq(other: Value): BooleanValue =
        if (other is BooleanValue) {
            BooleanValue(value == other.value)
        } else {
            throw UnsupportedOperationException("Cannot check equality between $this and $other")
        }

    override fun not(): BooleanValue = BooleanValue(!value)

    override fun and(other: Value): BooleanValue =
        if (other is BooleanValue) {
            BooleanValue(value && other.value)
        } else {
            throw UnsupportedOperationException("Cannot perform AND operation between $this and $other")
        }

    override fun or(other: Value): BooleanValue =
        if (other is BooleanValue) {
            BooleanValue(value || other.value)
        } else {
            throw UnsupportedOperationException("Cannot perform OR operation between $this and $other")
        }
}
