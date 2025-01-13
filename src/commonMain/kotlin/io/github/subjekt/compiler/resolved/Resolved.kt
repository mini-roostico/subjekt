/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.resolved

import io.github.subjekt.compiler.yaml.Configuration

/**
 * Represents a resolved outcome.
 */
sealed class ResolvedOutcome(
    /**
     * The message of the outcome.
     */
    open val message: String,
) {
    /**
     * Represents a resolved warning.
     */
    data class Warning(
        override val message: String,
    ) : ResolvedOutcome(message)

    /**
     * Represents a resolved error.
     */
    data class Error(
        override val message: String,
    ) : ResolvedOutcome(message)
}

/**
 * Represents a defined call in one of its possible values.
 */
data class DefinedCall(
    /**
     * The identifier of the call.
     */
    val identifier: String,
    /**
     * The list of arguments of the call.
     */
    val argumentsIdentifiers: List<String>,
    /**
     * Body to resolved when the call is triggered.
     */
    val body: Resolvable,
)

/**
 * Represents a defined parameter in one of its possible values.
 */
data class ResolvedParameter(
    /**
     * The identifier of the parameter.
     */
    val identifier: String,
    /**
     * The value of the parameter.
     */
    val value: Any,
)

/**
 * Represents a resolved subject. [name] and [code] are no longer [Resolvable] because all the possible values have
 * been collapsed in multiple [ResolvedSubject]s.
 */
data class ResolvedSubject(
    /**
     * The name of the subject.
     */
    val name: String,
    /**
     * The code of the subject.
     */
    val code: String,
    /**
     * The list of resolved outcomes.
     */
    val outcomes: List<ResolvedOutcome>,
    /**
     * The list of resolved arbitrary properties.
     */
    val properties: Map<String, String> = emptyMap(),
)

/**
 * Represents a resolved suite, final result of the compilation.
 */
data class ResolvedSuite(
    /**
     * The name of the suite.
     */
    val name: String,
    /**
     * The set of resolved subjects.
     */
    val subjects: Set<ResolvedSubject>,
    /**
     * The configuration of the suite.
     */
    val configuration: Configuration,
)
