/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.nodes.suite

/**
 * Represents a parameter node.
 */
data class Parameter(
    /**
     * The identifier of the parameter.
     */
    val name: String,
    /**
     * The list of values of the parameter. Each parameter can have multiple values, but these can't be resolvable.
     * Instead, they must be already defined values.
     */
    val values: List<Any>,
) {
    /**
     * Companion object for the [Parameter] class.
     */
    companion object {
        /**
         * Creates a Parameter node from a YAML [parameter] parsed data class.
         */
        fun fromYamlParameter(parameter: io.github.subjekt.compiler.yaml.Parameter): Parameter {
            require(parameter.values == null && parameter.value == null) {
                "Illegal parameter definition. Expected 'values' or 'value' in $parameter"
            }
            return Parameter(parameter.name, parameter.values ?: listOf(parameter.value!!))
        }
    }
}
