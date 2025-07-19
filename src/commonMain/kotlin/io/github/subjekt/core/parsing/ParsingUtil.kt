package io.github.subjekt.core.parsing

import io.github.subjekt.core.value.BooleanValue
import io.github.subjekt.core.value.FloatValue
import io.github.subjekt.core.value.IntValue
import io.github.subjekt.core.value.StringValue
import io.github.subjekt.core.value.Value
import io.github.subjekt.utils.Utils.parsingCheck

object ParsingUtil {
    internal fun parseParameterValues(value: Any?): List<Value> {
        parsingCheck(
            value is List<*> || value is String || value is Int || value is Boolean || value is Double,
        ) { "Parameter values must be a string or list" }
        return when (value) {
            is List<*> -> value.mapNotNull { it?.let(::parseParameterValue) }
            else -> listOf(parseParameterValue(value))
        }
    }

    private fun parseParameterValue(value: Any?): Value {
        parsingCheck(
            value is String || value is Int || value is Boolean || value is Double,
        ) { "Parameter value must be a string, int, boolean, or double" }
        val valueClass = value!!::class
        return when (valueClass) {
            String::class -> StringValue(value as String)
            Int::class -> IntValue(value as Int)
            Boolean::class -> BooleanValue(value as Boolean)
            Double::class -> FloatValue(value as Double)
            else -> throw IllegalArgumentException("Unsupported parameter value type: ${valueClass.simpleName}")
        }
    }
}
