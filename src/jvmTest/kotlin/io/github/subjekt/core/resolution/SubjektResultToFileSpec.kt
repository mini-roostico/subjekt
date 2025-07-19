/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.resolution

import io.github.subjekt.TestingUtility.getSimpleResolvedSubject
import io.github.subjekt.core.Configuration
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Suite
import io.github.subjekt.core.SymbolTable
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path

class SubjektResultToFileSpec : StringSpec({

    "toFile should write the correct content to a file" {
        val resolvedSubject = getSimpleResolvedSubject("subject1")
        val resolvedSuite =
            ResolvedSuite(Suite("suite", SymbolTable(), emptyList<Subject>(), Configuration()), setOf(resolvedSubject))
        val result =
            TextResult(resolvedSuite, {
                it.name
                    ?.value
                    ?.castToString()
                    ?.value
                    .orEmpty()
            })

        val filePath = "test.txt"
        result.toFile(filePath) shouldBe null
        Files.exists(Path(filePath)) shouldBe true
        Files.delete(Path(filePath))
    }

    "toFiles should write the correct content to multiple files" {
        val resolvedSubject1 = getSimpleResolvedSubject("subject1")
        val resolvedSubject2 = getSimpleResolvedSubject("subject2")
        val resolvedSuite =
            ResolvedSuite(
                Suite("suite", SymbolTable(), emptyList<Subject>(), Configuration()),
                setOf(resolvedSubject1, resolvedSubject2),
            )
        val result =
            TextResult(resolvedSuite, {
                it.name
                    ?.value
                    ?.castToString()
                    ?.value
                    .orEmpty()
            })

        val directory = "testDir"
        result.toFiles(directory) shouldBe null
        Files.exists(Path(directory)) shouldBe true
        File(directory).deleteRecursively()
    }
})
