/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.compiler.nodes.suite.Parameter
import io.github.subjekt.compiler.utils.Permutations.permute
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PermutationTest : StringSpec({
    "Simple parameter permutation" {
        val par1 = Parameter("test", listOf("1", "2"))
        val par2 = Parameter("test2", listOf("3", "4"))
        val result = mutableSetOf<String>()
        listOf(par1, par2).permute {
            result.add(it.joinToString(separator = "") { it.value.toString() })
        }
        val expected = setOf("13", "14", "23", "24")
        result shouldBe expected
    }

    "One single parameter list" {
        val par1 = Parameter("test", listOf("1", "2"))
        val par2 = Parameter("test2", listOf("3"))
        val result = mutableSetOf<String>()
        listOf(par1, par2).permute {
            result.add(it.joinToString(separator = "") { it.value.toString() })
        }
        val expected = setOf("13", "23")
        result shouldBe expected
    }

    "Both single parameter list" {
        val par1 = Parameter("test", listOf("1"))
        val par2 = Parameter("test2", listOf("3"))
        val result = mutableSetOf<String>()
        listOf(par1, par2).permute {
            result.add(it.joinToString(separator = "") { it.value.toString() })
        }
        val expected = setOf("13")
        result shouldBe expected
    }

    "Simple permutations of Iterable-Iterable-String" {
        val list = listOf(listOf(1, 2), listOf(3, 4, 5))
        val expected =
            listOf(
                listOf(1, 3),
                listOf(1, 4),
                listOf(1, 5),
                listOf(2, 3),
                listOf(2, 4),
                listOf(2, 5),
            )
        list.permute() shouldBe expected
    }
})
