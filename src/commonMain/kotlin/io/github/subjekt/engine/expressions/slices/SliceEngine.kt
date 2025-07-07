package io.github.subjekt.engine.expressions.slices

import io.github.subjekt.core.Parameter
import io.github.subjekt.engine.expressions.SliceSymbol

/**
 * Slice engine for handling slice operations in the Subjekt language.
 *
 * This engine provides functionality to apply slice operations on parameters based on the defined slice symbol.
 */
object SliceEngine {
    /**
     * Applies the slice operation defined by the [SliceSymbol] to the given [Parameter].
     */
    fun SliceSymbol.view(parameter: Parameter): Parameter =
        parameter.values.slice(startIndex, endIndex, stepIndex).run {
            Parameter(
                id = identifier,
                values = this,
            )
        }

    /**
     * Performs a Python-like slice of a list of elements based on the provided start, end, and step indices.
     */
    internal fun <T> List<T>.slice(
        start: Int? = null,
        end: Int? = null,
        step: Int = 1,
    ): List<T> {
        require(step != 0) { "Step cannot be zero" }
        if (isEmpty()) return emptyList()

        val size = this.size

        fun normalizeIndex(index: Int?): Int? =
            when {
                index == null -> null
                index < 0 -> maxOf(0, size + index)
                else -> minOf(index, size)
            }

        val result = mutableListOf<T>()

        when {
            step > 0 -> {
                val startIdx = normalizeIndex(start) ?: 0
                val endIdx = normalizeIndex(end) ?: size

                var i = startIdx
                while (i < endIdx && i < size) {
                    if (i >= 0) result.add(this[i])
                    i += step
                }
            }
            step < 0 -> {
                val startIdx = normalizeIndex(start) ?: (size - 1)
                val endIdx = normalizeIndex(end) ?: -1

                var i = startIdx
                while (i > endIdx && i >= 0) {
                    if (i < size) result.add(this[i])
                    i += step
                }
            }
        }

        return result
    }
}
