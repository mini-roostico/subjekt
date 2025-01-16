/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.parsing

import io.github.subjekt.TestingUtility.getOrFail
import io.github.subjekt.core.Source
import io.github.subjekt.core.Suite
import io.github.subjekt.core.parsing.SuiteFactory.parseIntoSuite
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe

class SuiteParsingSpec : FreeSpec({

    fun parse(yaml: String): Result<Suite> = Source.fromYaml(yaml).parseIntoSuite()

    "Simple suite parsing with name and one subject" - {
        val synonyms =
            table(
                headers("yaml"),
                row(
                    """
                    |name: "Simple suite"
                    |subjects:
                    |- name: "Simple subject"
                    """.trimMargin(),
                ),
                row(
                    """
                    |name: "Simple suite"
                    |subject:
                    |  name: "Simple subject"
                    """.trimMargin(),
                ),
                row(
                    """
                    |name: "Simple suite"
                    |subjects:
                    |- "Simple subject"
                    """.trimMargin(),
                ),
                row(
                    """
                    |name: "Simple suite"
                    |subject:
                    |  "Simple subject"
                    """.trimMargin(),
                ),
            )
        forAll(synonyms) { yaml ->
            "should work with yaml: $yaml" {
                val result = parse(yaml).getOrFail()
                result.id shouldBe "Simple suite"
                result.subjects.size shouldBe 1
                result.subjects[0].id shouldBe 0
                result.subjects[0].name?.source shouldBe "Simple subject"
            }
        }
    }

    "Simple suite parsing with name and multiple subjects" - {
        val result =
            parse(
                """
            |name: "Simple suite"
            |subjects:
            |- name: "Simple subject 1"
            |- name: "Simple subject 2"
                """.trimMargin(),
            ).getOrFail()

        result.id shouldBe "Simple suite"
        result.subjects.size shouldBe 2
    }
})
