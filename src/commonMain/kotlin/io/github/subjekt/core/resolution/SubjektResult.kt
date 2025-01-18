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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * A class that represents the result of a resolution process that has been exported via an [Exporter].
 * The type [T] represent the type of the content that will be obtained from each [ResolvedSubject] in the
 * [ResolvedSuite].
 */
sealed class SubjektResult<T>(
    /**
     * The [ResolvedSuite] that has been resolved and that will be exported.
     */
    val resolvedSuite: ResolvedSuite,
    /**
     * A function that will be used to extract the content from a [ResolvedSubject].
     */
    private val contentMapper: (ResolvedSubject) -> T,
    /**
     * A function that will be used to extract the name of a [ResolvedSubject], used as file name for example.
     */
    private val nameMapper: (ResolvedSubject) -> String = { it.name?.value ?: it.subjectId.toString() },
) {
    /**
     * Renders a single [element] of type [T] into a string.
     */
    protected abstract fun renderOne(element: T): String

    /**
     * Renders a list of [elements] of type [T] into a string.
     */
    protected abstract fun renderAll(elements: List<T>): String

    /**
     * Returns a string representation of the whole [ResolvedSuite].
     */
    fun asString(): String = renderAll(resolvedSuite.resolvedSubjects.map { contentMapper(it) })

    /**
     * Returns a list of string representations of each [ResolvedSubject] in the [ResolvedSuite].
     */
    fun asStrings(): List<String> = resolvedSuite.resolvedSubjects.map { renderOne(contentMapper(it)) }

    /**
     * Writes the whole [ResolvedSuite] to a file at the given [path].
     */
    fun toFile(path: String): Result<Unit> =
        renderAll(resolvedSuite.resolvedSubjects.map { contentMapper(it) }).writeTo(path, false)

    /**
     * Writes each [ResolvedSubject] in the [ResolvedSuite] to a file in the given [directory]. The file name for each
     * is obtained through the [nameMapper] function.
     */
    fun toFiles(directory: String): Result<Unit> =
        resolvedSuite.resolvedSubjects
            .map {
                renderOne(contentMapper(it)).writeTo("$directory/${nameMapper(it)}", false)
            }.firstOrNull { it.isFailure } ?: Result.success(Unit)
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
    private val separator: String = "\n",
    nameResolver: (ResolvedSubject) -> String = { it.name?.value ?: it.subjectId.toString() },
) : SubjektResult<String>(resolvedSuite, contentResolver, nameResolver) {
    override fun renderOne(element: String): String = element

    override fun renderAll(elements: List<String>): String = elements.joinToString(separator)
}

/**
 * A class that represents the result of a resolution process that has been exported as a JSON string.
 */
class JsonResult<T>(
    private val serializer: KSerializer<T>,
    resolvedSuite: ResolvedSuite,
    contentResolver: (ResolvedSubject) -> T,
    nameResolver: (ResolvedSubject) -> String = { it.name?.value ?: it.subjectId.toString() },
) : SubjektResult<T>(resolvedSuite, contentResolver, nameResolver) {
    override fun renderOne(element: T): String = Json.encodeToString(serializer, element)

    override fun renderAll(elements: List<T>): String = Json.encodeToString(ListSerializer(serializer), elements)
}
