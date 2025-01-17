/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.utils.Utils.buildRegex
import io.github.subjekt.utils.Utils.format

/**
 * Core entity used to represent a resolvable value. This is used to represent the values that can be resolved inside
 * a Suite with a proper [io.github.subjekt.core.definition.Context].
 */
class Resolvable
    @Throws(IllegalArgumentException::class)
    constructor(
        /**
         * Source from which the resolvable is parsed.
         */
        val source: String,
        expressionPrefix: String = "\${{",
        expressionSuffix: String = "}}",
    ) {
        private val resolvableString: ResolvableString =
            source.parseToResolvableString(
                expressionPrefix,
                expressionSuffix,
            )

        /**
         * Expressions contained in this Resolvable.
         *
         * **Note**: these are **unique** expressions, i.e., if the same expression is used multiple times in the
         * source, it will be counted only once.
         */
        val expressions: List<RawExpression>
            get() = resolvableString.expressions

        /**
         * Returns a string representation of this [Resolvable] as a formattable string (i.e., containing blocks `{0}`).
         */
        internal fun asFormattableString(): String = resolvableString.toFormat

        override fun toString(): String = "Resolvable(source='$source', expressions=$expressions)"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Resolvable) return false

            if (source != other.source) return false
            if (resolvableString != other.resolvableString) return false
            if (expressions != other.expressions) return false

            return true
        }

        override fun hashCode(): Int {
            var result = source.hashCode()
            result = 31 * result + resolvableString.hashCode()
            result = 31 * result + expressions.hashCode()
            return result
        }

        /**
         * Internal class to handle string substitution with expressions.
         */
        internal data class ResolvableString(
            /**
             * The string that can be formatted with the resolved expressions.
             */
            val toFormat: String,
            /**
             * The list of [RawExpression]s that can be resolved inside this string.
             *
             * **Note**: these are **unique** expressions, i.e., if the same expression is used multiple times in the
             * source, it will be counted only once.
             */
            val expressions: List<RawExpression>,
        ) {
            /**
             * Formats the string with the given [values]. Throws an [IllegalArgumentException] if the [values] number
             * it not equal to the number of [expressions].
             */
            @Throws(IllegalArgumentException::class)
            fun format(vararg values: Any): String {
                require(values.size == expressions.size) {
                    "Number of values does not match number of expressions"
                }
                return toFormat.format(*values)
            }
        }

        /**
         * Simple utility class to represent an expression that has not been parsed yet.
         */
        data class RawExpression(
            val source: String,
        )

        companion object {
            /**
             * Parses a string into a [ResolvableString] object. This is used to parse a string that contains
             * expressions into an intermediate class that handles string substitution and other operations.
             */
            @Throws(IllegalArgumentException::class)
            internal fun String.parseToResolvableString(
                expressionPrefix: String = "\${{",
                expressionSuffix: String = "}}",
            ): ResolvableString {
                // Match prefix ... suffix blocks
                val regex = buildRegex(expressionPrefix, expressionSuffix)
                val foundBlocks = mutableListOf<String>()
                val replaced =
                    regex.replace(this) {
                        val block = it.groupValues[1].trim()
                        // Note: collapse identical expressions to the same index
                        val index =
                            foundBlocks.indexOf(block).takeIf { it != -1 } ?: run {
                                foundBlocks.add(block)
                                foundBlocks.size - 1
                            }
                        "{{$index}}"
                    }
                return ResolvableString(replaced, foundBlocks.map { RawExpression(it) })
            }
        }
    }
