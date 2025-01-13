/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class SourceSpec : StringSpec({

    /**
     * Utility function to get the value of a Result, printing the exception if the result is a failure.
     */
    fun Result<Map<String, Any>>.getOrFail(): Map<String, Any> {
        if (this.isFailure) {
            fail("This exception originated the failure:\n\n${this.exceptionOrNull()}")
        }
        return this.getOrNull()!!
    }

    forAll(
        row(
            "a simple structure",
            """
            |name: "Subjekt"
            |isAwesome: true
            |howAwesome: 0
            """.trimMargin(),
            """
                |{
                |  "name": "Subjekt",
                |  "isAwesome": true,
                |  "howAwesome": 0
                |}
            """.trimMargin(),
            mapOf(
                "name" to "Subjekt",
                "isAwesome" to true,
                "howAwesome" to 0,
            ),
        ),
        row(
            "a nested structure",
            """
                |name: "Subjekt"
                |nested:
                |  2nested:
                |     a: 1
                |
            """.trimMargin(),
            """
                |{
                |  "name": "Subjekt",
                |  "nested": {
                |    "2nested": {
                |       "a": 1
                |    }
                |  }
                |}
            """.trimMargin(),
            mutableMapOf<String, Any>(
                "name" to "Subjekt",
            ).also {
                it["nested"] = mapOf("2nested" to mapOf("a" to 1))
            },
        ),
        row(
            "a list",
            """
                |name: "Subjekt"
                |list:
                |  - 1
                |  - 2
                |  - 3
            """.trimMargin(),
            """
                |{
                |  "name": "Subjekt",
                |  "list": [1, 2, 3]
                |}
            """.trimMargin(),
            mapOf(
                "name" to "Subjekt",
                "list" to listOf(1, 2, 3),
            ),
        ),
        row(
            "a list of maps",
            """
                |name: "Subjekt"
                |list:
                |  - a: 1
                |  - b: 2
                |  - c: 3
            """.trimMargin(),
            """
                |{
                |  "name": "Subjekt",
                |  "list": [
                |    {"a": 1},
                |    {"b": 2},
                |    {"c": 3}
                |  ]
                |}
            """.trimMargin(),
            mapOf(
                "name" to "Subjekt",
                "list" to
                    listOf(
                        mapOf("a" to 1),
                        mapOf("b" to 2),
                        mapOf("c" to 3),
                    ),
            ),
        ),
    ) { description, yaml, json, expected ->

        "A YAML source with $description should be parsed correctly to a map" {
            val source = Source.fromYaml(yaml)
            val map = source.extract().getOrFail()

            map shouldBe expected
        }

        "A JSON source with $description should be parsed correctly to a map" {
            val source = Source.fromJson(json)
            val map = source.extract().getOrFail()

            map shouldBe expected
        }
    }

    "An invalid YAML source should return a failure" {
        val source = Source.fromYaml("invalid")
        val result = source.extract()

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldStartWith "Cannot cast String to Map<String, Any?>"
    }

    "An invalid JSON source should return a failure" {
        val source = Source.fromJson("invalid")
        val result = source.extract()

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldStartWith "Unexpected JSON token at offset 0:"
    }

    "A YAML source with null values should return a failure" {
        val source = Source.fromYaml("key: null")
        val result = source.extract()

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldStartWith "Cannot use null values in Subjekt: key -> null"
    }

    "A JSON source with null values should return a failure" {
        val source = Source.fromJson("""{"key": null}""")
        val result = source.extract()

        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldStartWith "Cannot use null values in Subjekt: key -> null"
    }
})
