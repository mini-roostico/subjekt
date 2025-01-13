/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.utils

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlin.collections.component1
import kotlin.collections.component2

/**
 * Converts a JSON string into a map. Throws an exception if the JSON is not valid.
 */
internal fun String.toJsonMap(): Map<String, JsonElement> {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString(
        MapSerializer(String.serializer(), JsonElement.serializer()),
        this,
    )
}

private fun JsonElement.toAny(): Any? =
    when (this) {
        is JsonPrimitive ->
            when {
                this.isString -> this.content
                this.booleanOrNull != null -> this.boolean
                this.intOrNull != null -> this.int
                this.doubleOrNull != null -> this.double
                else -> null
            }
        is JsonObject -> this.toAnyMap()
        is JsonArray -> this.map { it.toAny() }
    }

/**
 * Converts a Map of (String, JsonElement) into a Map of (String?, Any?). Additional nullability checks are not done.
 */
internal fun Map<String, JsonElement>.toAnyMap(): Map<String?, Any?> =
    this.mapValues { (_, value) ->
        value.toAny()
    }
