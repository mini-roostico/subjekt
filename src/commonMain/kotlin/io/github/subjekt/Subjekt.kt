/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.utils.MessageCollector

/**
 * The main entry point for the Subjekt DSL
 */
object Subjekt {
    /**
     * Reporter used to collect messages from the compiler.
     */
    val reporter: MessageCollector = MessageCollector.SimpleCollector()

    /**
     * Subjekt entry point where to specify the sources to be used..
     */
    fun subjekt(block: SubjektContext.() -> Unit): SubjektContext = SubjektContext().apply(block)
}
