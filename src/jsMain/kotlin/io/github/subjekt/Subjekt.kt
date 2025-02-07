/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.core.Macro
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Source
import io.github.subjekt.core.resolution.ResolvedSuite
import io.github.subjekt.core.resolution.SubjektResult

/**
 * Subjekt entry class for using the Subjekt library. Various customization options can be specified by calling the
 * methods of this class.
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
class Subjekt internal constructor(
    source: Source,
) : AbstractSubjekt(source) {
    /**
     * Resolves the [source] into the [resolvedSuite] and the returns the [SubjektResult] containing the map of the
     * generation graph (i.e. a map with the subject IDs a keys and the list of the related resolved subjects as
     * values).
     */
    fun getGenerationGraph(): SubjektResult<dynamic, Map<Int, List<String>>>? =
        mapAndExport(
            identityMapper,
            generationGraphJsonExporter,
        )

    /**
     * Resolves the [source] into the [resolvedSuite] and the returns the
     * [io.github.subjekt.core.resolution.SubjektResult] containing the map of instances taken from each
     * [io.github.subjekt.core.resolution.ResolvedSubject].
     */
    fun resolveSubjectsAsJson(): SubjektResult<Map<String, String>, List<Map<String, String>>>? =
        mapAndExport(
            identityMapper,
            mapJsonExporter,
        )

    /**
     * Gets the [ResolvedSuite] and resolves it if not already resolved.
     */
    fun getResolvedSuite(): ResolvedSuite? = resolvedSuite

    /**
     * Adds a custom parameter to the [initialSymbolTable] used to resolve the [source].
     */
    fun customParameter(
        id: String,
        values: Array<String>,
    ): Subjekt {
        initialSymbolTable = initialSymbolTable.defineParameter(Parameter(id, values.toList()))
        return this
    }

    /**
     * Adds a custom macro to the [initialSymbolTable] used to resolve the [source].
     */
    fun customMacro(
        id: String,
        argumentsIdentifiers: Array<String>,
        values: Array<String>,
    ): Subjekt {
        initialSymbolTable =
            initialSymbolTable.defineMacro(Macro(id, argumentsIdentifiers.toList(), values.map { Resolvable(it) }))
        return this
    }

    /**
     * Adds a custom function to the [initialSymbolTable] used to resolve the [source].
     */
    fun customFunction(
        id: String,
        function: (List<String>) -> String,
    ): Subjekt {
        initialSymbolTable = initialSymbolTable.defineFunction(id, function)
        return this
    }

    companion object {
        /**
         * Creates a [Subjekt] instance from a YAML string containing the configuration.
         */
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun fromYaml(yaml: String): Subjekt = Subjekt(Source.Companion.fromYaml(yaml))

        /**
         * Creates a [Subjekt] instance from a JSON string containing the configuration.
         */
        @OptIn(ExperimentalJsStatic::class)
        @JsStatic
        fun fromJson(json: String): Subjekt = Subjekt(Source.Companion.fromJson(json))
    }
}
