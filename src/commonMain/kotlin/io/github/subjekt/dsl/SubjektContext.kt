/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.dsl

/**
 * Main context used in the DSL.
 */
class SubjektContext {
    private val sources = mutableListOf<SubjektSource>()

    // todo
//    /**
//     * Adds a source YAML file at path [pathToFile] to be compiled.
//     */
//    fun addSource(pathToFile: String) {
//        sources.add(SubjektSource(pathToFile))
//    }

    /**
     * Returns a list of all sources added to the context.
     */
    fun getSources(): List<SubjektSource> = sources.toList()
}
