/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.nodes.suite

import io.github.subjekt.compiler.yaml.Configuration

/**
 * Represents a suite node, entry point for the visitors.
 */
data class Suite(
    /**
     * The name of the suite.
     */
    val name: String,
    /**
     * The list of subjects in the suite.
     */
    val subjects: List<Subject>,
    /**
     * The configuration of the suite.
     */
    val configuration: Configuration = Configuration(),
    /**
     * The list of macros defined in the suite.
     */
    val macros: List<Macro> = emptyList(),
    /**
     * The list of global parameters defined in the suite.
     */
    val parameters: List<Parameter> = emptyList(),
    /**
     * The list of imports to include in the suite.
     */
    val imports: List<String> = emptyList(),
) {
    /**
     * Companion object for the [Suite] class.
     */
    companion object {
        /**
         * Creates a Suite node from a YAML [yamlSuite] parsed data class.
         */
        fun fromYamlSuite(yamlSuite: io.github.subjekt.compiler.yaml.Suite): Suite {
            val config = yamlSuite.config ?: Configuration()
            return Suite(
                yamlSuite.name,
                yamlSuite.subjects?.map { Subject.fromYamlSubject(it, config) } ?: emptyList(),
                config,
                yamlSuite.macros?.map { Macro.fromYamlMacro(it, config) } ?: emptyList(),
                yamlSuite.parameters?.map { Parameter.fromYamlParameter(it) } ?: emptyList(),
                yamlSuite.import ?: emptyList(),
            )
        }
    }
}
