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
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SuiteParsingSpec : StringSpec({

    fun parse(yaml: String): Result<Suite> = Source.fromYaml(yaml).parseIntoSuite()

    "Simple suite parsing with name and one subject" {
        val result =
            parse(
                """
            |name: "Simple suite"
            |subjects:
            |- name: "Simple subject"
                """.trimMargin(),
            ).getOrFail()

        result.id shouldBe "Simple suite"
        result.subjects.size shouldBe 1
    }
})
