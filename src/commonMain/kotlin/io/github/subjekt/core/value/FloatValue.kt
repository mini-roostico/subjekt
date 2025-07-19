package io.github.subjekt.core.value

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class FloatValue(
    val value: Double,
) : Value(Type.FLOAT) {
    override fun toString(): String = value.toString()

    override fun cast(targetType: Type): Value =
        when (targetType) {
            Type.STRING -> StringValue(value.toString())
            Type.INTEGER -> IntValue(value.toInt())
            Type.FLOAT -> this
            Type.BOOLEAN -> BooleanValue(value != 0.0)
            else -> throw UnsupportedOperationException("Cannot cast float value $this to $targetType.")
        }

    override fun eq(other: Value): BooleanValue =
        when (other) {
            is FloatValue -> BooleanValue(value == other.value)
            is IntValue -> BooleanValue(value == other.value.toDouble())
            is BooleanValue -> BooleanValue(value != 0.0 == other.value)
            else -> throw UnsupportedOperationException("Cannot compare float value $this with $other.")
        }

    override fun plus(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.plus(this)
                    ?: StringValue(value.toString() + other.value)
            is IntValue -> FloatValue(value + other.value.toDouble())
            is BooleanValue ->
                throw UnsupportedOperationException("Cannot add boolean value $other to float: $this")
            is FloatValue -> FloatValue(value + other.value)
            is ObjectValue ->
                throw UnsupportedOperationException("Cannot add object value $other to float: $this")
        }

    override fun minus(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.let {
                        this - it
                    }
                    ?: throw IllegalArgumentException("Cannot subtract $other from $this")
            is IntValue -> FloatValue(value - other.value)
            is BooleanValue ->
                throw IllegalArgumentException("Cannot subtract boolean value $other from float: $this")
            is FloatValue -> FloatValue(value - other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot add object value $other to float: $this")
        }

    override fun times(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.times(this)
                    ?: throw IllegalArgumentException("Cannot multiply $this by $other")
            is IntValue -> FloatValue(value * other.value)
            is BooleanValue -> throw IllegalArgumentException("Cannot multiply boolean value $other with $this")
            is FloatValue -> FloatValue(value * other.value)
            is ObjectValue -> throw UnsupportedOperationException(
                "Cannot multiply object value $other with float: $this",
            )
        }

    override fun div(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.div(this)
                    ?: throw IllegalArgumentException("Cannot divide $this by $other")
            is IntValue ->
                if (other.value == 0) {
                    throw ArithmeticException("Division by zero")
                } else {
                    FloatValue(value / other.value)
                }
            is BooleanValue -> throw IllegalArgumentException("Cannot divide boolean value $other by $this")
            is FloatValue -> FloatValue(value / other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot divide object value $other by $this")
        }

    override fun rem(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.rem(this)
                    ?: throw IllegalArgumentException("Cannot modulo $this by $other")
            is IntValue ->
                if (other.value == 0) {
                    throw ArithmeticException("Division by zero")
                } else {
                    IntValue((value % other.value).toInt())
                }
            is BooleanValue -> throw IllegalArgumentException("Cannot modulo boolean value $other with $this")
            is FloatValue -> FloatValue(value % other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot modulo object value $other by $this")
        }

    override fun unaryMinus(): Value = FloatValue(-value)

    override fun compareTo(other: Value): Int =
        when (other) {
            is FloatValue -> value.compareTo(other.value)
            is IntValue -> value.compareTo(other.value.toDouble())
            else -> throw UnsupportedOperationException("Cannot compare float value $this with $other.")
        }
}
