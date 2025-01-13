/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.utils.toAnyMap
import io.github.subjekt.utils.toJsonMap
import net.mamoe.yamlkt.Yaml

/**
 * A domain object that provides all the Suite's data. Can be created from a YAML or JSON string or file.
 */
sealed class Source {
    /**
     * The text content of the source.
     */
    abstract val text: String

    /**
     * Extracts the source content into a map. Returns `null` if the source is not valid.
     */
    abstract fun extract(): Result<Map<String, Any>>

    /**
     * Companion object that provides factory methods to create a [Source] from a YAML or JSON string or file.
     */
    companion object {
        /**
         * Creates a [Source] from a YAML string.
         */
        fun fromYaml(text: String): Source = YamlSource(text)

        /**
         * Creates a [Source] from a JSON string.
         */
        fun fromJson(text: String): Source = JsonSource(text)
    }
}

private fun <K, V> Map<K?, V?>.checkNulls(): Map<K, V> =
    map { (key, value) ->
        require(key != null && value != null) { "Cannot use null values in Subjekt: $key -> $value" }
        key to value
    }.toMap()

/**
 * Represents a Source created by parsing a YAML string. Can be created using [Source.fromYaml].
 */
class YamlSource(
    override val text: String,
) : Source() {
    override fun extract(): Result<Map<String, Any>> =
        runCatching {
            val rawMap = Yaml.decodeMapFromString(text)
            rawMap.checkNulls()
        }
}

/**
 * Represents a Source created by parsing a JSON string. Can be created using [Source.fromJson].
 */
class JsonSource(
    override val text: String,
) : Source() {
    override fun extract(): Result<Map<String, Any>> =
        runCatching {
            text.toJsonMap().toAnyMap().checkNulls()
        }
}
