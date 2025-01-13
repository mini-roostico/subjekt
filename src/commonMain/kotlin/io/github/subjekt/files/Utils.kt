/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.files

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString

/**
 * Cleans a name by removing all non-alphanumeric characters.
 */
fun cleanName(name: String): String = name.replace(Regex("[^A-Za-z0-9 ]"), "")

/**
 * Reads the text from a file at [path]. Returns `null` if the file does not exist.
 */
fun readText(path: String): String? =
    runCatching {
        SystemFileSystem.source(Path(path)).use { source ->
            return source.buffered().use {
                it.readString()
            }
        }
    }.getOrNull()

/**
 * Writes the [String] to a file at [path]. Returns a [Result] with the result of the operation.
 */
fun String.writeTo(
    path: String,
    append: Boolean = true,
): Result<Unit> =
    runCatching {
        SystemFileSystem.sink(Path(path), append).use { sink ->
            sink.buffered().use {
                it.writeString(this)
            }
        }
    }
