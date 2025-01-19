/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions

import io.github.subjekt.core.definition.DefinedMacro

/**
 * Calls the [DefinedMacro] with the given [arguments], internally resolving the macro's
 * [io.github.subjekt.core.Resolvable].
 */
fun DefinedMacro.call(arguments: List<String>): String = value.resolve(arguments)
