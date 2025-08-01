/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.core.value.StringValue
import io.github.subjekt.core.value.Value
import io.github.subjekt.utils.Utils.isLegalIdentifier
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a symbol that can be contained inside a [SymbolTable]. Can be a [Parameter] or a [Macro].
 */
sealed class Symbol

/**
 * Represents a parameter in a Suite or Subject. Parameters are used to define the values that can be used in a Suite or
 * Subject.
 *
 * Note: differently from the macros, parameters values are not [Resolvable]s, so their value is always a constant.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Parameter(
    /**
     * The unique identifier of the Parameter. This is used to reference the Parameter in the symbol table.
     */
    val id: String,
    /**
     * The values that the parameter can assume. These are the possible values that can be used in the [Resolvable]s.
     */
    val values: List<Value>,
) : Symbol() {
    companion object {
        /**
         * Keys that can be used as synonyms for [DEFAULT_ID_KEY].
         */
        val PARAMETER_NAME_KEYS = setOf("name", "id", "identifier", "title")

        /**
         * The default key used to indicate the ID of the parameter.
         */
        const val DEFAULT_ID_KEY = "id"

        /**
         * Keys that can be used as synonyms for [DEFAULT_VALUES_KEY].
         */
        val PARAMETER_VALUES_KEYS = setOf("values", "value", "val", "v", "bodies")

        /**
         * The default key used to indicate the values of the parameter.
         */
        const val DEFAULT_VALUES_KEY = "values"

        /**
         * Utility function to create a [Parameter] from a pair of ID and value.
         */
        fun Pair<String, String>.toSingleValueParameter(): Parameter {
            val (id, value) = this
            return Parameter(id, listOf(StringValue(value)))
        }

        /**
         * Utility function to create a [Parameter] from a pair of ID and list of values.
         */
        fun Pair<String, List<Value>>.toParameter(): Parameter {
            val (id, values) = this
            return Parameter(id, values)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Parameter) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * Utility typealias to represent a macro definition. A macro definition is a [Pair] where the first values represent
 * the Macro identifier and the second represent the arguments' identifiers.
 */
internal typealias MacroDefinition = Pair<String, List<String>>

/**
 * Represents a macro in a Suite or Subject. Macros can accept arguments and returns [Resolvable]s that internally use
 * their arguments to resolve to multiple [resolvables]. Each of these [resolvables] can therefore resolve to multiple
 * outputs, leading to a combinatorial explosion of possible outputs.
 *
 * *Note*: macro are unique by [id] and number of arguments (i.e. size of [argumentsIdentifiers]), so two macros with
 * the same [id] and different number of arguments are considered different.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Macro(
    /**
     * The unique identifier of the Macro. This is used to reference the Macro inside the symbol table.
     */
    val id: String,
    /**
     * The list of identifiers of the arguments that the Macro accepts. These are used to reference the arguments in the
     * Macro's resolvables.
     */
    val argumentsIdentifiers: List<String>,
    /**
     * The list of resolvables that the Macro can return. Each of these resolvables can resolve to multiple outputs.
     */
    val resolvables: List<Resolvable>,
) : Symbol() {
    companion object {
        /**
         * Keys that can be used as synonyms for [DEFAULT_ID_KEY].
         */
        val MACRO_NAME_KEYS = setOf("name", "id", "identifier", "title", "def", "definition", "fun", "function")

        /**
         * The default key used to indicate the ID of the macro.
         */
        const val DEFAULT_ID_KEY = "id"

        /**
         * Keys that can be used as synonyms for [DEFAULT_RESOLVABLES_KEY].
         */
        val MACRO_RESOLVABLES_KEYS =
            setOf("resolvables", "resolvable", "res", "r", "bodies", "body", "values", "value", "v")

        /**
         * The default key used to indicate the resolvables of the macro.
         */
        const val DEFAULT_RESOLVABLES_KEY = "resolvables"

        /**
         * Utility function to create a [Macro] from a pair of ID and list of arguments, with [resolvables] as values.
         */
        fun MacroDefinition.toActualMacro(resolvables: List<Resolvable>): Macro {
            val (id, arguments) = this
            return Macro(id, arguments, resolvables)
        }

        /**
         * Converts a string to a macro definition.
         *
         * The string must be in the form `macroId(arg1, arg2, arg3)`.
         */
        internal fun String.asMacroDefinition(): MacroDefinition {
            require(get(0).isLetter()) { "Macro identifier must start with a letter" }
            val trimmed = replace(" ", "")
            require(trimmed.isLegalIdentifier('_', '(', ')', ',')) {
                "Macro definition can contain alphanumeric characters or one of the following: _(),"
            }
            if (!trimmed.contains("(")) {
                require(!trimmed.contains(')') && !trimmed.contains(',')) {
                    "Illegal macro definition. Macro identifier cannot contain ')' or ','"
                }
                return Pair(this, emptyList())
            }
            val identifier = trimmed.substringBefore("(")
            val argsString = trimmed.substringAfter("(")
            require(argsString.contains(')')) { "Illegal macro definition. Expected ')' in $this" }
            val arguments =
                argsString
                    .substringBefore(")")
                    .split(",")
                    .filter(String::isNotBlank)
            require(arguments.all { it.isLegalIdentifier() }) {
                "In $this: Macro arguments must start with a letter and can contain only alphanumeric characters or _"
            }
            return Pair(identifier, arguments)
        }

        /**
         * Utility function to create a [Macro] from a string ID with [resolvables] as values.
         *
         * **IMPORTANT**: the arguments are assumed to be empty, they are NOT parsed from the string. For that, use
         * [asMacroDefinition].
         */
        fun String.asMacro(resolvable: List<Resolvable>): Macro = Macro(this, emptyList(), resolvable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Macro) return false

        if (id != other.id) return false
        if (argumentsIdentifiers.size != other.argumentsIdentifiers.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + argumentsIdentifiers.size.hashCode()
        return result
    }
}

/**
 * Represents a function that can be contained inside a [SymbolTable].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class SubjektFunction(
    val id: String,
    private val function: Function1<List<Value>, Value>,
) : Symbol() {
    /**
     * Calls the function with the given [arguments].
     */
    operator fun invoke(arguments: List<Value>): Value = function(arguments)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubjektFunction) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun fromStringFunction(
            id: String,
            function: Function1<List<String>, String>,
        ): SubjektFunction =
            SubjektFunction(id) { args ->
                val stringArgs = args.map { it.castToString().value }
                StringValue(function(stringArgs))
            }
    }
}
