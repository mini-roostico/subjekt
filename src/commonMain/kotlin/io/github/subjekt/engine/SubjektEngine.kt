/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine

import io.github.subjekt.core.Suite
import io.github.subjekt.core.resolution.ResolvedSuite
import io.github.subjekt.engine.impl.SubjektEngineImpl

/**
 * The [SubjektEngine] interface defines the contract for evaluating a [Suite] and producing a [ResolvedSuite].
 * It is the main entry point for the Subjekt engine, allowing users to parse and resolve Subjekt suites.
 */
interface SubjektEngine {
    /**
     * Evaluates the given [suite] and returns a [ResolvedSuite] containing all the resolved subjects.
     */
    fun evaluate(suite: Suite): ResolvedSuite

    companion object {
        /**
         * The default [SubjektEngine] implementation.
         */
        val Default: SubjektEngine = SubjektEngineImpl()
    }
}
