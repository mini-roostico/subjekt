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
 * A module is a collection of symbols (parameters, macros and functions) that can be imported and used in other
 * modules.
 */
data class Module(
    /**
     * The unique identifier of the module.
     */
    val id: String,
    /**
     * Symbol table of the module, containing all the symbols defined in the module.
     */
    val symbolTable: SymbolTable,
    /**
     * Optional string path to where the module is located. This is used to resolve relative imports.
     */
    val path: String?,
)
