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
import io.github.subjekt.utils.Logger
import io.github.subjekt.utils.MessageCollector
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.maps.shouldBeEmpty
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
            val suite = parse(yaml).getOrFail()
            suite.id shouldBe "Simple suite"
            suite.subjects.size shouldBe 1
            suite.subjects[0].id shouldBe 0
            suite.subjects[0].name?.source shouldBe "Simple subject"
        }
    }

    "Simple suite parsing with name and multiple subjects" - {
        val synonyms =
            table(
                headers("yaml"),
                row(
                    """
                    |name: "Simple suite"
                    |subjects:
                    |- name: "Simple subject 1"
                    |- name: "Simple subject 2"
                    """.trimMargin(),
                ),
                row(
                    """
                    |name: "Simple suite"
                    |subjects:
                    |- "Simple subject 1"
                    |- "Simple subject 2"
                    """.trimMargin(),
                ),
                row(
                    """
                    |name: "Simple suite"
                    |subject:
                    |- name: "Simple subject 1"
                    |- name: "Simple subject 2"
                    """.trimMargin(),
                ),
                row(
                    """
                    |name: "Simple suite"
                    |subject:
                    |- "Simple subject 1"
                    |- "Simple subject 2"
                    """.trimMargin(),
                ),
            )
        forAll(synonyms) { yaml ->
            val suite = parse(yaml).getOrFail()
            suite.id shouldBe "Simple suite"
            suite.subjects.size shouldBe 2
            suite.subjects[0].id shouldBe 0
            suite.subjects[0].name?.source shouldBe "Simple subject 1"
            suite.subjects[1].id shouldBe 1
            suite.subjects[1].name?.source shouldBe "Simple subject 2"
        }
    }

    "Suite parsing with subjects and simple configuration" - {
        val suite =
            parse(
                """
               |name: "Simple suite"
               |config:
               |  expressionPrefix: "{"
               |  expressionSuffix: "}"
               |subjects:
               |- name: "Simple subject {test} and {test2}"
               |- id: "Simple subject {test} 2"
               |  code: "code {test}"
                """.trimMargin(),
            ).getOrFail()

        suite.id shouldBe "Simple suite"
        suite.configuration.expressionPrefix shouldBe "{"
        suite.configuration.expressionSuffix shouldBe "}"
        suite.subjects.size shouldBe 2
        suite.subjects[0].id shouldBe 0
        suite.subjects[0].name?.source shouldBe "Simple subject {test} and {test2}"
        suite.subjects[0].name?.asFormattableString() shouldBe "Simple subject {{0}} and {{1}}"
        suite.subjects[0]
            .name
            ?.expressions
            ?.map { it.source } shouldBe listOf("test", "test2")
        suite.subjects[1].id shouldBe 1
        suite.subjects[1].name?.source shouldBe "Simple subject {test} 2"
        suite.subjects[1].name?.asFormattableString() shouldBe "Simple subject {{0}} 2"
        suite.subjects[1]
            .name
            ?.expressions
            ?.map { it.source } shouldBe listOf("test")
        suite.subjects[1].resolvables["code"]?.source shouldBe "code {test}"
        suite.subjects[1]
            .resolvables["code"]
            ?.expressions
            ?.map { it.source } shouldBe listOf("test")
    }

    "Suite parsing with custom and nested configuration fields" - {
        val suite =
            parse(
                """
                |name: "Simple suite"
                |configuration:
                |   custom:
                |       myField: 1
                |       myField2: "2"
                |   linting: "true"
                |subject:
                |  "Dummy subject"
                """.trimMargin(),
            ).getOrFail()
        suite.id shouldBe "Simple suite"
        Logger.messageCollector.messages
            .filter { it.type == MessageCollector.MessageType.WARNING }
            .size shouldBe 1
        Logger.messageCollector.messages
            .first()
            .message shouldBe
            "Adding a non-default configuration key 'custom' to the suite."
        suite.configuration["custom"] shouldBe mapOf("myField" to 1, "myField2" to 2)
        suite.configuration.lint shouldBe true
        suite.subjects.size shouldBe 1
    }

    "Suite parsing with global parameters" - {
        val suite =
            parse(
                """
                |name: "Simple suite"
                |parameters:
                |  - id: "param1"
                |    values:
                |    - "value1"
                |    - "value2"
                |  - name: "param2"
                |    values:
                |    - "value3"
                |    - "value4"
                |subject: "Test"
                """.trimMargin(),
            ).getOrFail()
        suite.id shouldBe "Simple suite"
        suite.symbolTable.parameters.size shouldBe 2
        suite.symbolTable.resolveParameter("param1")?.values shouldBe listOf("value1", "value2")
        suite.symbolTable.resolveParameter("param2")?.values shouldBe listOf("value3", "value4")
    }

    "Suite parsing with local parameters" - {
        val suite =
            parse(
                """
                |name: "Simple suite"
                |subjects:
                |  - name: "Simple subject"
                |    parameters:
                |      - id: "param1"
                |        values:
                |        - "value1"
                |        - "value2"
                |      - name: "param2"
                |        values:
                |        - "value3"
                |        - "value4"
                """.trimMargin(),
            ).getOrFail()
        suite.id shouldBe "Simple suite"
        val subject = suite.subjects[0]
        subject.id shouldBe 0
        subject.name?.source shouldBe "Simple subject"
        subject.symbolTable.parameters.size shouldBe 2
        subject.symbolTable.resolveParameter("param1")?.values shouldBe listOf("value1", "value2")
        subject.symbolTable.resolveParameter("param2")?.values shouldBe listOf("value3", "value4")
        suite.symbolTable.parameters.shouldBeEmpty()
        suite.symbolTable.macros.shouldBeEmpty()
    }
})
