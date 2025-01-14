/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.impl

import io.github.subjekt.core.Configuration
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Suite
import io.github.subjekt.core.SymbolTable

/**
 * Implementation of the [Suite] interface.
 */
internal class SuiteImpl(
    override val id: String,
    override val symbolTable: SymbolTable,
    override val subjects: List<Subject>,
    override val configuration: Configuration,
) : Suite
