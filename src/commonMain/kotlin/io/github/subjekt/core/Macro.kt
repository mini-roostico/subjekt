/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.utils.Utils.isLegalIdentifier

/**
 * Utility typealias to represent a macro definition. A macro definition is a [Pair] where the first values represent
 * the Macro identifier and the second represent the arguments' identifiers.
 */
internal typealias MacroDefinition = Pair<String, List<String>>

/**
 * Represents a macro in a Suite or Subject. Macros can accept arguments and returns [Resolvable]s that internally use
 * their arguments to resolve to multiple [resolvables]. Each of these [resolvables] can therefore resolve to multiple
 * outputs, leading to a combinatorial explosion of possible outputs.
 */
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
) {
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
        fun MacroDefinition.toMacro(resolvables: List<Resolvable>): Macro {
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
        fun String.toMacro(resolvable: List<Resolvable>): Macro = Macro(this, emptyList(), resolvable)
    }
}
