/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class ResolvableSpec : StringSpec({

    "A Resolvable should be built correctly from a simple string" {
        val resolvable = Resolvable("Hello World!")
        resolvable.source shouldBe "Hello World!"
        resolvable.expressions.shouldBeEmpty()
        resolvable.asFormattableString() shouldBe "Hello World!"
    }

    "A Resolvable should be built correctly from a string with an expression" {
        val resolvable = Resolvable("Hello \${{name}}!")
        resolvable.source shouldBe "Hello \${{name}}!"
        resolvable.expressions.size shouldBe 1
        resolvable.expressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}!"
    }

    "A Resolvable should be built correctly from a string with multiple expressions" {
        val resolvable = Resolvable("Hello \${{ name }}! My name is \${{ myName }}.")
        resolvable.source shouldBe "Hello \${{ name }}! My name is \${{ myName }}."
        resolvable.expressions.size shouldBe 2
        resolvable.expressions[0].source shouldBe "name"
        resolvable.expressions[1].source shouldBe "myName"
        resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{1}}."
    }

    "A Resolvable should be built correctly from a string with expressions containing newlines" {
        val resolvable = Resolvable("Hello \${{ \nname\n}}!")
        resolvable.source shouldBe "Hello \${{ \nname\n}}!"
        resolvable.expressions.size shouldBe 1
        resolvable.expressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}!"
    }

    "A Resolvable should be built correctly from a string with expressions containing newlines and spaces" {
        val resolvable = Resolvable("Hello \${{ \n name \n}}!")
        resolvable.source shouldBe "Hello \${{ \n name \n}}!"
        resolvable.expressions.size shouldBe 1
        resolvable.expressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}!"
    }

    "A Resolvable should be built correctly from a string with expressions containing newlines and spaces and " +
        "multiple expressions" {
            val resolvable = Resolvable("Hello \${{ \n name \n}}! My name is \${{ myName }}.")
            resolvable.source shouldBe "Hello \${{ \n name \n}}! My name is \${{ myName }}."
            resolvable.expressions.size shouldBe 2
            resolvable.expressions[0].source shouldBe "name"
            resolvable.expressions[1].source shouldBe "myName"
            resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{1}}."
        }

    "A Resolvable should be built correctly when expressions are repeated" {
        val resolvable = Resolvable("Hello \${{name}}! My name is \${{name}}.")
        resolvable.source shouldBe "Hello \${{name}}! My name is \${{name}}."
        resolvable.expressions.size shouldBe 1
        resolvable.expressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{0}}."
    }

    "A Resolvable should be built correctly when using custom expression delimiters" {
        val resolvable = Resolvable("Hello #name#! My name is #myName#.", "#", "#")
        resolvable.source shouldBe "Hello #name#! My name is #myName#."
        resolvable.expressions.size shouldBe 2
        resolvable.expressions[0].source shouldBe "name"
        resolvable.expressions[1].source shouldBe "myName"
        resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{1}}."
    }
})
