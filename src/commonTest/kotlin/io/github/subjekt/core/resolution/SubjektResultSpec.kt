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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

class SubjektResultSpec : StringSpec({

    "asString should return the correct string representation" {
        val resolvedSubject = getSimpleResolvedSubject("subject1")
        val resolvedSuite =
            ResolvedSuite(Suite("suite", SymbolTable(), emptyList<Subject>(), Configuration()), setOf(resolvedSubject))
        val result = TextResult(resolvedSuite, { it.name?.value ?: "" })

        result.asString() shouldBe "subject1"
    }

    "asStrings should return the correct list of string representations" {
        val resolvedSubject1 = getSimpleResolvedSubject("subject1")
        val resolvedSubject2 = getSimpleResolvedSubject("subject2")
        val resolvedSuite =
            ResolvedSuite(
                Suite("suite", SymbolTable(), emptyList<Subject>(), Configuration()),
                setOf(resolvedSubject1, resolvedSubject2),
            )
        val result = TextResult(resolvedSuite, { it.name?.value ?: "" })

        result.asStrings() shouldBe listOf("subject1", "subject2")
    }

    "JsonResult should return the correct JSON string representation" {
        val resolvedSubject = getSimpleResolvedSubject("subject1")
        val resolvedSuite =
            ResolvedSuite(Suite("suite", SymbolTable(), emptyList<Subject>(), Configuration()), setOf(resolvedSubject))
        val result =
            JsonResult(
                MapSerializer(String.serializer(), String.serializer()),
                ListSerializer(MapSerializer(String.serializer(), String.serializer())),
                resolvedSuite,
                {
                    mapOf(
                        "name" to (it.name?.value.orEmpty()),
                    )
                },
                { map -> map },
            )

        result.asString() shouldBe """[{"name":"subject1"}]"""
    }
})
