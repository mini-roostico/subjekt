/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.files

import java.io.File

/**
 * Reads the text from a file at [path]. Returns `null` if the file does not exist.
 */
fun readText(path: String): String? =
    runCatching {
        File(path).readText()
    }.getOrNull()

/**
 * Writes the [String] to a file at [path]. Returns a [Result] with the result of the operation.
 */
fun String.writeTo(
    path: String,
    append: Boolean = true,
): Result<Unit> =
    runCatching {
        File(path).also {
            if (append) {
                it.appendText(this)
            } else {
                it.writeText(this)
            }
        }
    }
