/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.yaml

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.utils.MessageCollector

/**
 * Reader for YAML files.
 */
object Reader {
    /**
     * Parse a YAML from a file at [filePath] into a [Suite] object. It reports errors to the [messageCollector].
     * Returns the [Suite] object parsed from the YAML file, or null if an error occurred.
     */
    fun suiteFromYamlFile(
        filePath: String,
        messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
    ): Suite? =
        try {
            TODO()
        } catch (e: Exception) {
            messageCollector.error(
                "Failed to parse YAML file: $filePath. Error: ${e.message}",
                Context.Companion.emptyContext(),
                -1,
            )
            null
        }

    /**
     * Parse a YAML [yaml] string into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite]
     * object parsed from the YAML string, or null if an error occurred.
     */
    fun suiteFromYaml(
        yaml: String,
        messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
    ): Suite? =
        try {
            TODO()
        } catch (e: Exception) {
            messageCollector.error(
                "Failed to parse YAML suite: \n$yaml\n\nError: ${e.message}",
                Context.Companion.emptyContext(),
                -1,
            )
            null
        }
}
