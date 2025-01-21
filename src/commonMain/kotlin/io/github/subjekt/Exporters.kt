/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.core.resolution.Exporter
import io.github.subjekt.core.resolution.JsonResult
import io.github.subjekt.core.resolution.ResolvedSubject
import io.github.subjekt.utils.Utils.uniqueName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.PairSerializer
import kotlinx.serialization.builtins.serializer

/**
 * A name resolver, useful for [io.github.subjekt.core.resolution.SubjektResult], that generates a unique name for
 * a [ResolvedSubject] if the name is not present.
 */
val safeNameResolverUniqueFallback: (ResolvedSubject) -> String = {
    it.name?.value ?: it.uniqueName()
}

/**
 * Utility class to create an [Exporter] that produces [JsonResult], where [singleSerializer] is the serializer for the
 * result of exporting one [ResolvedSubject], [reduceSerializer] is the serializer for the final result. The other
 * parameters are functions that will be used to extract the content from a [ResolvedSubject], see
 * [io.github.subjekt.core.resolution.SubjektResult] documentation for more details.
 */
fun <I, R> createJsonExporter(
    singleSerializer: KSerializer<I>,
    reduceSerializer: KSerializer<R>,
    contentResolver: (ResolvedSubject) -> I,
    reduce: (List<I>) -> R,
    nameResolver: (ResolvedSubject) -> String = safeNameResolverUniqueFallback,
): Exporter<I, R> =
    Exporter {
        JsonResult(
            singleSerializer,
            reduceSerializer,
            it,
            contentResolver,
            reduce,
            nameResolver,
        )
    }

/**
 * A simple [Exporter] that produces a [JsonResult] containing a map of [String] keys and [String] values, obtained
 * from the [ResolvedSubject] instances.
 */
val mapJsonExporter: Exporter<Map<String, String>, List<Map<String, String>>> =
    createJsonExporter(
        MapSerializer(String.serializer(), String.serializer()),
        ListSerializer(MapSerializer(String.serializer(), String.serializer())),
        { it.instances.mapValues { (key, instance) -> instance.value } },
        { map -> map },
    )

/**
 * A simple [Exporter] that produces a [JsonResult] containing a map of [Int] keys and [List] of [String] values,
 * where the [Int] key is the subjectId of the [io.github.subjekt.core.Subject], and the [List] of [String] values is
 * the list of [ResolvedSubject] names obtained from resolving that subject.
 */
val generationGraphJsonExporter: Exporter<Pair<Int, String>, Map<Int, List<String>>> =
    createJsonExporter(
        PairSerializer(Int.serializer(), String.serializer()),
        MapSerializer(Int.serializer(), ListSerializer(String.serializer())),
        { it.subjectId to safeNameResolverUniqueFallback(it) },
        { subjectIdToName ->
            subjectIdToName.groupBy({ it.first }, { it.second })
        },
    )
