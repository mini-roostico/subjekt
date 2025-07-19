package io.github.subjekt.core.value

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class FloatValue(
    val value: Double,
) : Value(Type.FLOAT) {
    override fun toString(): String = "float($value)"

    override fun cast(targetType: Type): Value =
        when (targetType) {
            Type.STRING -> StringValue(value.toString())
            Type.INTEGER -> IntValue(value.toInt())
            Type.FLOAT -> this
            Type.BOOLEAN -> BooleanValue(value != 0.0)
            else -> throw UnsupportedOperationException("Cannot cast float value ${this.value} to $targetType.")
        }

    override fun eq(other: Value): BooleanValue =
        when (other) {
            is FloatValue -> BooleanValue(value == other.value)
            is IntValue -> BooleanValue(value == other.value.toDouble())
            is BooleanValue -> BooleanValue(value != 0.0 == other.value)
            else -> throw UnsupportedOperationException("Cannot compare float value ${this.value} with $other.")
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
                throw UnsupportedOperationException("Cannot add boolean value ${other.value} to float: ${this.value}")
            is FloatValue -> FloatValue(value + other.value)
            is ObjectValue ->
                throw UnsupportedOperationException("Cannot add object value $other to float: ${this.value}")
        }

    override fun minus(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.let {
                        this - it
                    }
                    ?: throw IllegalArgumentException("Cannot subtract ${other.value} from ${this.value}")
            is IntValue -> FloatValue(value - other.value)
            is BooleanValue ->
                throw IllegalArgumentException("Cannot subtract boolean value ${other.value} from float: ${this.value}")
            is FloatValue -> FloatValue(value - other.value)
            is ObjectValue -> throw UnsupportedOperationException(
                "Cannot add object value $other to float: ${this.value}",
            )
        }

    override fun times(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.times(this)
                    ?: throw IllegalArgumentException("Cannot multiply ${this.value} by ${other.value}")
            is IntValue -> FloatValue(value * other.value)
            is BooleanValue -> throw IllegalArgumentException(
                "Cannot multiply boolean value ${other.value} with ${this.value}",
            )
            is FloatValue -> FloatValue(value * other.value)
            is ObjectValue -> throw UnsupportedOperationException(
                "Cannot multiply object value $other with float: ${this.value}",
            )
        }

    override fun div(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.div(this)
                    ?: throw IllegalArgumentException("Cannot divide ${this.value} by ${other.value}")
            is IntValue ->
                if (other.value == 0) {
                    throw ArithmeticException("Division by zero")
                } else {
                    FloatValue(value / other.value)
                }
            is BooleanValue -> throw IllegalArgumentException(
                "Cannot divide boolean value ${other.value} by ${this.value}",
            )
            is FloatValue -> FloatValue(value / other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot divide object value $other by ${this.value}")
        }

    override fun rem(other: Value): Value =
        when (other) {
            is StringValue ->
                other
                    .castOrNull(type)
                    ?.rem(this)
                    ?: throw IllegalArgumentException("Cannot modulo ${this.value} by ${other.value}")
            is IntValue ->
                if (other.value == 0) {
                    throw ArithmeticException("Division by zero")
                } else {
                    IntValue((value % other.value).toInt())
                }
            is BooleanValue -> throw IllegalArgumentException(
                "Cannot modulo boolean value ${other.value} with ${this.value}",
            )
            is FloatValue -> FloatValue(value % other.value)
            is ObjectValue -> throw UnsupportedOperationException("Cannot modulo object value $other by ${this.value}")
        }

    override fun unaryMinus(): Value = FloatValue(-value)

    override fun compareTo(other: Value): Int =
        when (other) {
            is FloatValue -> value.compareTo(other.value)
            is IntValue -> value.compareTo(other.value.toDouble())
            else -> throw UnsupportedOperationException("Cannot compare float value ${this.value} with $other.")
        }
}
