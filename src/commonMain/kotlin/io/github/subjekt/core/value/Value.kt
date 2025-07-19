package io.github.subjekt.core.value

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents the type of IR node.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
enum class Type {
    STRING,
    INTEGER,
    FLOAT,
    NUMBER,
    UNDEFINED,
    BOOLEAN,
    OBJECT,
}

@OptIn(ExperimentalJsExport::class)
@JsExport
@Suppress("TooManyFunctions")
sealed class Value(
    val type: Type,
) {
    fun castOrNull(targetType: Type): Value? =
        try {
            cast(targetType)
        } catch (_: Exception) {
            null
        }

    abstract fun cast(targetType: Type): Value

    fun castToString(): StringValue = cast(Type.STRING) as StringValue

    fun castToInt(): IntValue = cast(Type.INTEGER) as IntValue

    fun castToBoolean(): BooleanValue = cast(Type.BOOLEAN) as BooleanValue

    fun castToFloat(): FloatValue = cast(Type.FLOAT) as FloatValue

    infix fun concat(other: Value): Value = StringValue(castToString().value + other.castToString().value)

    infix fun le(other: Value): BooleanValue = BooleanValue(compareTo(other) <= 0)

    infix fun lt(other: Value): BooleanValue = BooleanValue(compareTo(other) < 0)

    infix fun ge(other: Value): BooleanValue = BooleanValue(compareTo(other) >= 0)

    infix fun gt(other: Value): BooleanValue = BooleanValue(compareTo(other) > 0)

    infix fun ne(other: Value): BooleanValue = !eq(other)

    abstract infix fun eq(other: Value): BooleanValue

    open operator fun plus(other: Value): Value =
        throw UnsupportedOperationException("Plus operation is not supported for $this and $other")

    open operator fun minus(other: Value): Value =
        throw UnsupportedOperationException("Minus operation is not supported for $this and $other")

    open operator fun times(other: Value): Value =
        throw UnsupportedOperationException("Times operation is not supported for $this and $other")

    open operator fun div(other: Value): Value =
        throw UnsupportedOperationException("Div operation is not supported for $this and $other")

    open operator fun rem(other: Value): Value =
        throw UnsupportedOperationException("Rem operation is not supported for $this and $other")

    open operator fun unaryMinus(): Value =
        throw UnsupportedOperationException("Unary minus operation is not supported for $this")

    open operator fun not(): BooleanValue =
        throw UnsupportedOperationException("Unary not operation is not supported for $this")

    open operator fun compareTo(other: Value): Int =
        throw UnsupportedOperationException("Comparison operation is not supported for $this and $other")

    open infix fun and(other: Value): BooleanValue =
        throw UnsupportedOperationException("And operation is not supported for $this and $other")

    open infix fun or(other: Value): BooleanValue =
        throw UnsupportedOperationException("Or operation is not supported for $this and $other")

    fun toIntOrNull(): Int? =
        when (this) {
            is IntValue -> value
            is StringValue -> value.toIntOrNull()
            is BooleanValue -> if (value) 1 else 0
            is FloatValue -> value.toInt()
            is ObjectValue -> null
        }

    fun toBooleanOrNull(): Boolean? =
        when (this) {
            is BooleanValue -> value
            is StringValue -> value.toBooleanStrictOrNull()
            is IntValue -> value != 0
            is FloatValue -> value != 0.0
            is ObjectValue -> null
        }

    fun toDoubleOrNull(): Double? =
        when (this) {
            is FloatValue -> value
            is IntValue -> value.toDouble()
            is StringValue -> value.toDoubleOrNull()
            is BooleanValue -> if (value) 1.0 else 0.0
            is ObjectValue -> null
        }

    companion object {
        fun ofString(value: String): Value = StringValue(value)

        fun ofInt(value: Int): Value = IntValue(value)

        fun ofBoolean(value: Boolean): Value = BooleanValue(value)

        fun ofDouble(value: Double): Value = FloatValue(value)

        fun String.asStringValue(): Value = StringValue(this)

        fun Int.asIntValue(): Value = IntValue(this)

        fun Boolean.asBooleanValue(): Value = BooleanValue(this)

        fun Double.asDoubleValue(): Value = FloatValue(this)
    }
}
