package io.github.subjekt.core.value

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class StringValue(
    val value: String,
) : Value(Type.STRING) {
    override fun toString(): String = "'$value'"

    override fun cast(targetType: Type): Value =
        when (targetType) {
            Type.STRING -> this
            Type.INTEGER ->
                IntValue(
                    value.toIntOrNull()
                        ?: throw IllegalArgumentException("Cannot cast '$value' to INTEGER."),
                )
            Type.FLOAT ->
                FloatValue(
                    value.toDoubleOrNull()
                        ?: throw IllegalArgumentException("Cannot cast '$value' to FLOAT."),
                )
            Type.BOOLEAN ->
                BooleanValue(
                    value.toBooleanStrictOrNull()
                        ?: throw IllegalArgumentException("Cannot cast '$value' to BOOLEAN."),
                )
            else -> throw UnsupportedOperationException("Cannot cast $this to $targetType.")
        }

    override fun eq(other: Value): BooleanValue = BooleanValue(other is StringValue && value == other.value)

    override operator fun plus(other: Value): Value =
        if (other is StringValue) {
            StringValue(value + other.value)
        } else {
            castOrNull(other.type)?.plus(other)
                ?: StringValue(value + other.toString())
        }

    override operator fun minus(other: Value): Value =
        if (other is StringValue) {
            throw UnsupportedOperationException("Cannot subtract $other from $this.")
        } else {
            castOrNull(other.type)?.minus(other)
                ?: throw UnsupportedOperationException("Cannot subtract $other from $this.")
        }

    override operator fun times(other: Value): Value =
        if (other is StringValue) {
            throw UnsupportedOperationException("Cannot multiply $this by $other.")
        } else {
            castOrNull(other.type)?.times(other)
                ?: throw UnsupportedOperationException("Cannot multiply $this by $other.")
        }

    override operator fun div(other: Value): Value =
        if (other is StringValue) {
            throw UnsupportedOperationException("Cannot divide $this by $other.")
        } else {
            castOrNull(other.type)?.div(other)
                ?: throw UnsupportedOperationException("Cannot divide $this by $other.")
        }

    override operator fun rem(other: Value): Value =
        if (other is StringValue) {
            throw UnsupportedOperationException("Cannot modulo $this by $other.")
        } else {
            castOrNull(other.type)?.rem(other)
                ?: throw UnsupportedOperationException("Cannot modulo $this by $other.")
        }

    override operator fun unaryMinus(): Value =
        throw UnsupportedOperationException("Cannot apply unary minus to StringValue.")

    override fun compareTo(other: Value): Int {
        if (other !is StringValue) {
            throw UnsupportedOperationException("Cannot compare $this with $other.")
        }
        return value.compareTo(other.value)
    }
}
