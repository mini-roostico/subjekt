/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.resolution

import io.github.subjekt.core.Subject.Companion.DEFAULT_NAME_KEY
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Represents a resolved Subject in a Suite. Resolved Subjects are the core results in Subjekt, containing all the
 * resolved instances of the Subject.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class ResolvedSubject(
    /**
     * The identifier of the Subject from which this [io.github.subjekt.core.resolution.ResolvedSubject] was resolved.
     */
    val subjectId: Int,
    /**
     * The resolved instances of the Subject. Each instance is a [Instance] object that contains the resolved value and
     * corresponds to exactly one [io.github.subjekt.core.Resolvable] in the original [io.github.subjekt.core.Subject].
     */
    val instances: Map<String, Instance>,
) {
    /**
     * Special getter for the name [Instance] of the Subject. Equal to doing `instances[DEFAULT_NAME_KEY]`.
     */
    val name: Instance?
        get() = instances[DEFAULT_NAME_KEY]
}
