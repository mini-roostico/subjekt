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
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.resolution.SubjektResult
import io.github.subjekt.core.value.StringValue

/**
 * Subjekt entry class for using the Subjekt library. Various customization options can be specified by calling the
 * methods of this class.
 */
class Subjekt internal constructor(
    source: Source,
) : AbstractSubjekt(source) {
    /**
     * Resolves the [source] into the [resolvedSuite] and the returns the [SubjektResult] containing the map of the
     * generation graph (i.e. a map with the subject IDs a keys and the list of the related resolved subjects as
     * values).
     */
    fun getGenerationGraph(): SubjektResult<Pair<Int, String>, Map<Int, List<String>>>? =
        mapAndExport(
            identityMapper,
            generationGraphJsonExporter,
        )

    /**
     * Adds a custom parameter to the [initialSymbolTable] used to resolve the [source].
     */
    fun customParameter(
        id: String,
        values: List<String>,
    ): Subjekt {
        initialSymbolTable = initialSymbolTable.defineParameter(Parameter(id, values.map { StringValue(it) }))
        return this
    }

    /**
     * Adds a list of custom parameters to the [initialSymbolTable] used to resolve the [source].
     */
    fun customParameters(parameters: List<Parameter>): Subjekt {
        initialSymbolTable = initialSymbolTable.defineParameters(parameters)
        return this
    }

    /**
     * Adds a custom macro to the [initialSymbolTable] used to resolve the [source].
     */
    fun customMacro(
        id: String,
        argumentsIdentifiers: List<String>,
        values: List<Resolvable>,
    ): Subjekt {
        initialSymbolTable = initialSymbolTable.defineMacro(Macro(id, argumentsIdentifiers, values))
        return this
    }

    /**
     * Adds a list of custom macros to the [initialSymbolTable] used to resolve the [source].
     */
    fun customMacros(macros: List<Macro>): Subjekt {
        initialSymbolTable = initialSymbolTable.defineMacros(macros)
        return this
    }

    /**
     * Adds a list of custom functions to the [initialSymbolTable] used to resolve the [source].
     */
    fun customFunctions(functions: List<SubjektFunction>): Subjekt {
        initialSymbolTable = initialSymbolTable.defineFunctions(functions)
        return this
    }

    companion object {
        /**
         * Creates a [Subjekt] instance from a YAML string containing the configuration.
         */
        fun fromYaml(yaml: String): Subjekt = Subjekt(Source.fromYaml(yaml))

        /**
         * Creates a [Subjekt] instance from a JSON string containing the configuration.
         */
        fun fromJson(json: String): Subjekt = Subjekt(Source.fromJson(json))
    }
}
