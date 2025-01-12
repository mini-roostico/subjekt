/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler

import io.github.subjekt.compiler.conversion.Stdlib
import io.github.subjekt.compiler.resolved.ResolvedSuite
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.compiler.visitors.SuiteVisitor
import io.github.subjekt.compiler.yaml.Reader.suiteFromYaml
import io.github.subjekt.compiler.yaml.Reader.suiteFromYamlFile
import io.github.subjekt.compiler.yaml.Suite

/**
 * Entry point for the compiler of Subjekt YAML suites. It provides methods to compile YAML code, files, and resources
 * into [ResolvedSuite]s.
 */
object SubjektCompiler {
    /**
     * Resolves a nullable [io.github.subjekt.compiler.nodes.suite.Suite] into a [ResolvedSuite] using the Subjekt compiler. It returns null if the suite is null
     * or if an error occurred during the resolution.
     */
    private fun Suite?.resolve(messageCollector: MessageCollector): ResolvedSuite? {
        if (this == null) {
            return null
        }
        val suite =
            io.github.subjekt.compiler.nodes.suite.Suite.Companion
                .fromYamlSuite(this)
        val visitor = SuiteVisitor(messageCollector, listOf(Stdlib))
        visitor.visitSuite(suite)
        return ResolvedSuite(
            suite.name,
            visitor.resolvedSubjects.filterNot { it.code.isBlank() || it.name.isBlank() }.toSet(),
            suite.configuration,
        )
    }

    /**
     * Compiles a YAML [code] string into a [ResolvedSuite]. It returns null if an error occurred during the compilation.
     */
    fun compile(
        code: String,
        messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
    ): ResolvedSuite? = suiteFromYaml(code, messageCollector).resolve(messageCollector)

    /**
     * Compiles a YAML file at [filePath] into a [ResolvedSuite]. It returns null if an error occurred during the
     * compilation.
     */
    fun compileFile(
        filePath: String,
        messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
    ): ResolvedSuite? = suiteFromYamlFile(filePath, messageCollector).resolve(messageCollector)
}
