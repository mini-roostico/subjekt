/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.resolution

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * A mapper is a function that can be applied to a resolved suite to transform it into another resolved suite.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun interface Mapper {
    /**
     * Maps a [resolvedSuite] into another resolved suite.
     */
    fun map(resolvedSuite: ResolvedSuite): ResolvedSuite
}
