/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.yaml

import io.github.subjekt.compiler.yaml.Macro
import io.github.subjekt.compiler.yaml.Outcome
import io.github.subjekt.compiler.yaml.Parameter
import io.github.subjekt.compiler.yaml.Reader
import io.github.subjekt.compiler.yaml.Subject
import io.github.subjekt.compiler.yaml.Suite
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ListHandlingTest : StringSpec({
    "should deserialize list of strings".config(enabled = false) {
        val yaml =
            """
            - one
            - two
            - three
            """.trimIndent()

        // val result = Reader.readYaml<List<String>>(yaml)
        val result = TODO()
        result shouldBe listOf("one", "two", "three")
    }

    "should deserialize list of integers".config(enabled = false) {
        val yaml =
            """
            - 1
            - 2
            - 3
            """.trimIndent()

        // val result = Reader.readYaml<List<Int>>(yaml)
        val result = TODO()
        result shouldBe listOf(1, 2, 3)
    }

    "should deserialize a single element list without dash".config(enabled = false) {
        val yamlSugar =
            """
            name: "suite"
            subjects:
              name: "subject"
              code: "this is code"
            """.trimIndent()
        val yaml =
            """
            name: "suite"
            subjects:
              - name: "subject"
                code: "this is code"
            """.trimIndent()

        val resultSugar = Reader.suiteFromYaml(yamlSugar)
        val result = Reader.suiteFromYaml(yaml)

        resultSugar shouldBe
            Suite("suite", null, null, listOf(Subject("subject", null, null, "this is code", null, null)), null)
        result shouldBe resultSugar
    }

    "should deserialize a full suite with syntactic sugar".config(enabled = false) {
        val yaml =
            """
            name: "suite"
            macros:
              def: "macro(param1, param2)"
              values:
                - "value1"
                - "value2"
            subjects:
              - name: "subject"
                parameters:
                  - name: "param"
                    values:
                      - "value"
                  - name: "param2"
                    value: "value2"
                  - name: "param3"
                    values: "value3"
                macros:
                  - def: "macro(param1)"
                    value: "value"
                code: "this is code"
                outcomes:
                  - warning: "warning"
                  - error: "error"
                properties:
                  key: "value"
            """.trimIndent()

        val result = Reader.suiteFromYaml(yaml)

        result shouldBe
            Suite(
                "suite",
                null,
                listOf(Macro("macro(param1, param2)", listOf("value1", "value2"), null)),
                listOf(
                    Subject(
                        "subject",
                        listOf(
                            Parameter("param", listOf("value"), null),
                            Parameter("param2", null, "value2"),
                            Parameter("param3", listOf("value3"), null),
                        ),
                        listOf(Macro("macro(param1)", null, "value")),
                        "this is code",
                        listOf(Outcome("warning", null), Outcome(null, "error")),
                        mapOf("key" to "value"),
                    ),
                ),
                null,
            )
    }
})
