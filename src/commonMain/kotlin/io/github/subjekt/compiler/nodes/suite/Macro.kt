/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.nodes.suite

import io.github.subjekt.compiler.resolved.Resolvable
import io.github.subjekt.compiler.yaml.Configuration
import kotlin.require

/**
 * Represents a macro definition node.
 */
class Macro(
    /**
     * The identifier of the macro.
     */
    val identifier: String,
    /**
     * The list of arguments identifiers.
     */
    val argumentsIdentifiers: List<String>,
    /**
     * The list of bodies of the macro. Each macro can have multiple, resolvable bodies.
     */
    val bodies: List<Resolvable>,
) {
    /**
     * The number of arguments the macro expects.
     */
    val argumentsNumber: Int
        get() = argumentsIdentifiers.size

    /**
     * Utility object to create a macro from a YAML macro.
     */
    companion object {
        /**
         * Creates a Macro node from a YAML [macro] parsed data class. The [config] is used to parse the bodies.
         */
        fun fromYamlMacro(
            macro: io.github.subjekt.compiler.yaml.Macro,
            config: Configuration,
        ): Macro {
            require(!macro.def.contains("(")) { "Illegal macro definition. Expected '(' in ${macro.def}" }

            val clean = macro.def.replace(" ", "")
            val identifier = clean.substringBefore("(")
            val arguments =
                clean
                    .substringAfter("(")
                    .substringBefore(")")
                    .split(",")
                    .filter(String::isNotBlank)
            require(macro.values == null && macro.value == null) {
                "Illegal macro definition. Expected 'values' or 'value' in $macro"
            }
            val bodies =
                macro.values?.map {
                    Template.parse(it, config.expressionPrefix, config.expressionSuffix)
                } ?: listOf(Template.parse(macro.value!!, config.expressionPrefix, config.expressionSuffix))
            return Macro(identifier, arguments, bodies)
        }
    }
}
