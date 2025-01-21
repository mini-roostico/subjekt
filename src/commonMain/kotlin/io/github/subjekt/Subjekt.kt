/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.compiler.resolve
import io.github.subjekt.core.Source
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.parsing.SuiteFactory.parseIntoSuite
import io.github.subjekt.core.resolution.ResolvedSuite
import io.github.subjekt.utils.Logger

/**
 * End to end utility for subjekt to compile a [Source] directly into a [ResolvedSuite].
 */
internal fun Source.compile(initialSymbolTable: SymbolTable): ResolvedSuite? {
    val parsingResult = parseIntoSuite(initialSymbolTable)
    if (parsingResult.isFailure) {
        Logger.error { parsingResult.exceptionOrNull()?.message.toString() }
        return null
    }
    val resolvingResult =
        runCatching {
            val resolvedSuite = parsingResult.getOrNull()?.resolve()
            return resolvedSuite ?: error("Failed to resolve the suite.")
        }
    if (resolvingResult.isFailure) {
        Logger.error { resolvingResult.exceptionOrNull()?.message.toString() }
        return null
    }
    return resolvingResult.getOrNull()
}
