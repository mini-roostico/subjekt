/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.core.resolution.Mapper
import io.github.subjekt.core.resolution.ResolvedSuite

/**
 * A mapper that maps a [ResolvedSuite] into itself.
 */
val identityMapper: Mapper = Mapper { suite: ResolvedSuite -> suite }
