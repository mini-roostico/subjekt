/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.files

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

class IOSpec : StringSpec({
    "Reading a file should return its content" {
        val expectedContent =
            """
            Lorem Impsum Dolor Sit Amet
            Consectetur Adipiscing Elit
            """.trimIndent()
        val path = "src/commonTest/resources/plain/TestFile.txt"
        val content = readText(path)
        content shouldBe expectedContent
    }

    "Writing to a file should create it and write the content" {
        val path = "src/commonTest/resources/plain/Temp.txt"
        val content = "Lorem Impsum Dolor Sit Amet"
        val result = content.writeTo(path, append = false)
        result.isSuccess shouldBe true
        val newContent = readText(path)
        newContent shouldBe content
        SystemFileSystem.delete(Path(path))
    }

    "Writing to an existing file should append to its content" {
        val path = "src/commonTest/resources/plain/TestFile.txt"
        val content = "Consectetur Adipiscing Elit"
        val previousContent = readText(path)!!
        val result = content.writeTo(path)
        result.isSuccess shouldBe true
        val newContent = readText(path)
        newContent shouldBe previousContent + content
        previousContent.writeTo(path, append = false)
    }
})
