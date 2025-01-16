/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.utils

import io.github.subjekt.utils.Utils.format
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StringFormatSpec : StringSpec({

    "A simple string with one argument should be formatted correctly" {
        val str = "Hello {{0}}"
        val formatted = str.format("World")
        formatted shouldBe "Hello World"
    }

    "A simple string with multiple arguments should be formatted correctly" {
        val str = "Hello {{0}}, my name is {{1}}"
        val formatted = str.format("World", "Subjekt")
        formatted shouldBe "Hello World, my name is Subjekt"
    }

    "A simple string with repeated arguments should be formatted correctly" {
        val str = "Hello {{0}}, my name is {{0}} and I am doing {{1}}"
        val formatted = str.format("World", "formatting")
        formatted shouldBe "Hello World, my name is World and I am doing formatting"
    }

    "A simple string with two arguments should be formatted partially with only one arguments" {
        val str = "Hello {{0}}, my name is {{1}}"
        val result = str.format("World")
        result shouldBe "Hello World, my name is {{1}}"
    }

    "A simple string with no arguments should return itself when formatted with arguments" {
        val str = "Hello"
        val result = str.format("World", "Subjekt")
        result shouldBe "Hello"
    }
})
