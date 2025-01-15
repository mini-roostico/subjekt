/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

/**
 * Core entity used to represent a resolvable value. This is used to represent the values that can be resolved inside
 * a Suite with a proper [io.github.subjekt.core.definition.Context].
 */
data class Resolvable(
    /**
     * Source from which the resolvable is parsed.
     */
    val source: String,
)
