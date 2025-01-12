/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.dsl

import io.github.subjekt.Subjekt
import io.github.subjekt.compiler.SubjektCompiler
import io.github.subjekt.compiler.resolved.ResolvedSubject
import io.github.subjekt.compiler.resolved.ResolvedSuite
import io.github.subjekt.compiler.yaml.Configuration

/**
 * A source of Subjekt [code].
 */
class SubjektSource(
    val code: String,
) {
    private val suite: ResolvedSuite? by lazy {
        SubjektCompiler.compile(code, Subjekt.reporter)
    }

    /**
     * The configuration of the suite.
     */
    val configuration: Configuration
        get() = suite?.configuration ?: Configuration()

    /**
     * Generates the subjects from the source YAML code.
     */
    fun getGeneratedSubjects(): Set<ResolvedSubject> =
        if (suite == null) {
            emptySet()
        } else {
            suite?.subjects ?: emptySet()
        }

    companion object {
        /**
         * Creates a [SubjektSource] from a YAML [file].
         */
        fun fromFile(filePath: String): SubjektSource = TODO()
    }
}
