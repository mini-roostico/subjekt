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
import io.github.subjekt.core.resolution.Exporter
import io.github.subjekt.core.resolution.Mapper
import io.github.subjekt.core.resolution.ResolvedSuite
import io.github.subjekt.core.resolution.SubjektResult
import io.github.subjekt.utils.Logger

/**
 * End to end utility for subjekt to compile a [Source] directly into a [ResolvedSuite].
 */
internal fun Source.compile(initialSymbolTable: SymbolTable): ResolvedSuite? {
    val parsingResult = parseIntoSuite(initialSymbolTable)
    parsingResult
        .getOrNull()
        ?.symbolTable
        ?.resolveParameter("par2")
        ?.values
        ?.forEach { println(it) }

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

/**
 * Subjekt entry class for using the Subjekt library. Various customization options can be specified by calling the
 * methods of this class.
 */
abstract class AbstractSubjekt internal constructor(
    private val source: Source,
) {
    protected var initialSymbolTable =
        SymbolTable()
            .defineParameters(stdLibParameters)
            .defineFunctions(stdLibFunctions)

    /**
     * The [io.github.subjekt.core.resolution.ResolvedSuite] obtained by parsing and resolving the [source].
     * If the parsing or the resolution fails, this property will be `null`.
     */
    val resolvedSuite: ResolvedSuite? by lazy {
        source.compile(initialSymbolTable)
    }

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
     * Customizable exporting behavior that resolves the [source] into the [resolvedSuite] and then maps and exports the
     * result using the provided [mapper] and [exporter].
     */
    fun <I, R> mapAndExport(
        mapper: Mapper,
        exporter: Exporter<I, R>,
    ): SubjektResult<I, R>? = resolvedSuite?.let { exporter.export(mapper.map(it)) }

    /**
     * Adds a custom function to the [initialSymbolTable] used to resolve the [source].
     */
    fun customFunction(
        id: String,
        function: (List<String>) -> String,
    ): AbstractSubjekt {
        initialSymbolTable = initialSymbolTable.defineFunction(id, function)
        return this
    }
}
