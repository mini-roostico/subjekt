/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.resolution

import io.github.subjekt.core.Suite

/**
 * Represents a resolved Suite. A [ResolvedSuite] contains the whole result of a Subjekt elaboration, working as a
 * container of the main result objects, the [ResolvedSubject]s.
 */
data class ResolvedSuite(
    /**
     * The original [Suite] from which this [ResolvedSuite] was resolved.
     */
    val originalSuite: Suite,
    /**
     * The resolved Subjects in the Suite. Each [ResolvedSubject] corresponds to a [io.github.subjekt.core.Subject] in
     * the original Suite.
     */
    val resolvedSubjects: List<ResolvedSubject>,
) {
    /**
     * The unique identifier for this [io.github.subjekt.core.resolution.ResolvedSuite] as it was for the original
     * [Suite].
     */
    val suiteId: String
        get() = originalSuite.id
}
