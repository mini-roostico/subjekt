/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.files

actual fun String.writeTo(
    path: String,
    append: Boolean,
): Result<Unit> = Result.failure(NotImplementedError("Writing to file is not yet supported in Subjekt for Javascript!"))
