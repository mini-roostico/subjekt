/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.parsing

import io.github.subjekt.core.Suite
import io.github.subjekt.core.parsing.SuiteFactory.SuiteBuilder

/**
 * Visitor class used to parse a map into a [Suite] instance.
 */
class MapVisitor {
    /**
     * Exception thrown when an error occurs during parsing.
     */
    class ParsingException(
        override val message: String?,
    ) : Exception()

    private var suiteBuilder: SuiteBuilder = SuiteBuilder()

    /**
     * Throws a [ParsingException] with the given message.
     */
    private fun parsingFail(message: () -> String): Nothing = throw ParsingException(message())

    /**
     * Visits a map and creates a [Suite] instance. This method is unsafe and should be encapsulated in a try-catch
     * block or `runCatching` block to handle parsing exceptions.
     */
    @Throws(IllegalArgumentException::class)
    fun visit(map: Map<String, Any>): Suite {
        map.entries.forEach { (key, value) ->
            visitGlobalLevel(key, value)
        }
        return runCatching {
            suiteBuilder.build()
        }.fold({
            it
        }) {
            parsingFail { "Invalid Suite structure: ${it.message}" }
        }
    }

    private fun visitGlobalLevel(
        key: String,
        value: Any,
    ) {
        when (key) {
            "name", "id" -> visitSuiteName(value)
            "configuration", "config" -> visitConfiguration(value)
            "parameters", "params" -> visitParameters(value)
            "macros" -> visitMacros(value)
            "imports", "include", "import" -> visitImports(value)
            "subjects" -> visitSubjects(value)
            else -> parsingFail { "Unknown Suite configuration key: $key" }
        }
    }

    private fun visitSubjects(subjects: Any) {
        TODO("Not yet implemented")
        println(subjects)
    }

    private fun visitImports(imports: Any) {
        TODO("Not yet implemented")
        println(imports)
    }

    private fun visitMacros(macros: Any) {
        TODO("Not yet implemented")
        println(macros)
    }

    private fun visitParameters(parameters: Any) {
        TODO("Not yet implemented")
        println(parameters)
    }

    private fun visitConfiguration(config: Any) {
        TODO()
        println(config)
    }

    private fun visitSuiteName(value: Any) {
        // suiteBuilder.name = value.toString()
        println(value)
    }
}
