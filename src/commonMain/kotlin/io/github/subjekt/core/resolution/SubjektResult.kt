/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.resolution

import io.github.subjekt.files.writeTo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * A class that represents the result of a resolution process that has been exported via an [Exporter].
 *
 * A [SubjektResult] can produce results from a single [ResolvedSubject] or from the whole [ResolvedSuite]. The first
 * exporting behavior can be specified with the [contentMapper] function, while the second exporting behavior can be
 * specified with the [reduce] function.
 *
 * The type [I] represent the type of the content that will be obtained from each [ResolvedSubject] in the
 * [ResolvedSuite].
 *
 * The type [R] represents the type of the final result that will be obtained from the whole [ResolvedSuite].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
sealed class SubjektResult<I, R>(
    /**
     * The [ResolvedSuite] that has been resolved and that will be exported.
     */
    val resolvedSuite: ResolvedSuite,
    /**
     * A function that will take the result extracted from [contentMapper] and convert it into the final result.
     */
    private val reduce: (List<I>) -> R,
    /**
     * A function that will be used to extract the content from a [ResolvedSubject].
     */
    private val contentMapper: (ResolvedSubject) -> I,
    /**
     * A function that will be used to extract the name of a [ResolvedSubject], used as file name for example.
     */
    private val nameMapper: (ResolvedSubject) -> String = {
        it.name
            ?.value
            ?.castToString()
            ?.value
            ?: it.subjectId.toString()
    },
) {
    /**
     * Renders a single [element] of type [I] into a string.
     */
    protected abstract fun renderOne(element: I): String

    /**
     * Renders a final result of type [R] into a string.
     */
    protected abstract fun renderAll(reduceResult: R): String

    /**
     * Returns a string representation of the whole [ResolvedSuite].
     */
    fun asString(): String = renderAll(reduce(resolvedSuite.resolvedSubjects.map { contentMapper(it) }))

    /**
     * Returns a list of string representations of each [ResolvedSubject] in the [ResolvedSuite].
     */
    fun asStrings(): List<String> = resolvedSuite.resolvedSubjects.map { renderOne(contentMapper(it)) }

    /**
     * Writes the whole [ResolvedSuite] to a file at the given [path]. Returns `null` if the operation is successful,
     * otherwise returns the error message.
     */
    fun toFile(path: String): String? =
        renderAll(reduce(resolvedSuite.resolvedSubjects.map { contentMapper(it) }))
            .writeTo(path, false)
            .takeIf {
                it.isFailure
            }?.exceptionOrNull()
            ?.message

    /**
     * Writes each [ResolvedSubject] in the [ResolvedSuite] to a file in the given [directory]. The file name for each
     * is obtained through the [nameMapper] function. Returns `null` if the operation is successful, otherwise returns
     * the first error message encountered.
     */
    fun toFiles(directory: String): String? =
        resolvedSuite.resolvedSubjects
            .map {
                renderOne(contentMapper(it)).writeTo("$directory/${nameMapper(it)}", false)
            }.firstOrNull { it.isFailure }
            ?.exceptionOrNull()
            ?.message
}

/**
 * A class that represents the result of a resolution process that has been exported as simple text.
 */
class TextResult(
    /**
     * Separator used to separate each [ResolvedSubject] in the [ResolvedSuite] when exporting the whole suite as a
     * whole.
     */
    resolvedSuite: ResolvedSuite,
    contentResolver: (ResolvedSubject) -> String,
    reduce: (List<String>) -> List<String> = { it },
    private val separator: String = "\n",
    nameResolver: (ResolvedSubject) -> String = {
        it.name
            ?.value
            ?.castToString()
            ?.value ?: it.subjectId.toString()
    },
) : SubjektResult<String, List<String>>(resolvedSuite, reduce, contentResolver, nameResolver) {
    override fun renderOne(element: String): String = element

    override fun renderAll(reduceResult: List<String>): String = reduceResult.joinToString(separator)
}

/**
 * A class that represents the result of a resolution process that has been exported as a JSON string.
 */
class JsonResult<I, R>(
    private val singleSerializer: KSerializer<I>,
    private val reduceSerializer: KSerializer<R>,
    resolvedSuite: ResolvedSuite,
    contentResolver: (ResolvedSubject) -> I,
    reduce: (List<I>) -> R,
    nameResolver: (ResolvedSubject) -> String = {
        it.name
            ?.value
            ?.castToString()
            ?.value ?: it.subjectId.toString()
    },
) : SubjektResult<I, R>(resolvedSuite, reduce, contentResolver, nameResolver) {
    override fun renderOne(element: I): String = Json.encodeToString(singleSerializer, element)

    override fun renderAll(reduceResult: R): String = Json.encodeToString(reduceSerializer, reduceResult)
}
