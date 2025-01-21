/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * Entry point of the whole Subjekt configuration. A global entity containing Parameters, Macros, Subjects and a
 * Configuration.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Suite(
    /**
     * The unique identifier of the Suite. This is used to reference the Suite in other Suites.
     */
    val id: String,
    /**
     * The symbol table used to resolve references in the Suite.
     */
    val symbolTable: SymbolTable,
    /**
     * The list of Subjects in the Suite.
     */
    val subjects: List<Subject>,
    /**
     * The configuration of the Suite.
     */
    val configuration: Configuration,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Suite) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}
