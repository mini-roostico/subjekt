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
 * Entry point of the whole Subjekt configuration. A global entity containing Parameters, Macros, Subjects and a
 * Configuration.
 */
interface Suite {
    /**
     * The unique identifier of the Suite. This is used to reference the Suite in other Suites.
     */
    val id: String

    /**
     * The symbol table used to resolve references in the Suite.
     */
    val symbolTable: SymbolTable

    /**
     * The list of Subjects in the Suite.
     */
    val subjects: Iterable<Subject>

    /**
     * The configuration of the Suite.
     */
    val configuration: Configuration
}
