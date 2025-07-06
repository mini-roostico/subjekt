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
        resolvable.rawExpressions.shouldBeEmpty()
        resolvable.asFormattableString() shouldBe "Hello World!"
        resolvable.resolveFormatting() shouldBe "Hello World!"
    }

    "A Resolvable should be built correctly from a string with an expression" {
        val resolvable = Resolvable("Hello \${{name}}!")
        resolvable.source shouldBe "Hello \${{name}}!"
        resolvable.rawExpressions.size shouldBe 1
        resolvable.rawExpressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}!"
        resolvable.resolveFormatting("Subjekt") shouldBe "Hello Subjekt!"
    }

    "A Resolvable should be built correctly from a string with multiple expressions" {
        val resolvable = Resolvable("Hello \${{ name }}! My name is \${{ myName }}.")
        resolvable.source shouldBe "Hello \${{ name }}! My name is \${{ myName }}."
        resolvable.rawExpressions.size shouldBe 2
        resolvable.rawExpressions[0].source shouldBe "name"
        resolvable.rawExpressions[1].source shouldBe "myName"
        resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{1}}."
        resolvable.resolveFormatting("Subjekt", "Francesco") shouldBe "Hello Subjekt! My name is Francesco."
    }

    "A Resolvable should be built correctly from a string with expressions containing newlines" {
        val resolvable = Resolvable("Hello \${{ \nname\n}}!")
        resolvable.source shouldBe "Hello \${{ \nname\n}}!"
        resolvable.rawExpressions.size shouldBe 1
        resolvable.rawExpressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}!"
        resolvable.resolveFormatting("Subjekt") shouldBe "Hello Subjekt!"
    }

    "A Resolvable should be built correctly from a string with expressions containing newlines and spaces" {
        val resolvable = Resolvable("Hello \${{ \n name \n}}!")
        resolvable.source shouldBe "Hello \${{ \n name \n}}!"
        resolvable.rawExpressions.size shouldBe 1
        resolvable.rawExpressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}!"
        resolvable.resolveFormatting("Subjekt") shouldBe "Hello Subjekt!"
    }

    "A Resolvable should be built correctly from a string with expressions containing newlines and spaces and " +
        "multiple expressions" {
            val resolvable = Resolvable("Hello \${{ \n name \n}}! My name is \${{ myName }}.")
            resolvable.source shouldBe "Hello \${{ \n name \n}}! My name is \${{ myName }}."
            resolvable.rawExpressions.size shouldBe 2
            resolvable.rawExpressions[0].source shouldBe "name"
            resolvable.rawExpressions[1].source shouldBe "myName"
            resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{1}}."
            resolvable.resolveFormatting("Subjekt", "Francesco") shouldBe "Hello Subjekt! My name is Francesco."
        }

    "A Resolvable should be built correctly when expressions are repeated" {
        val resolvable = Resolvable("Hello \${{name}}! My name is \${{name}}.")
        resolvable.source shouldBe "Hello \${{name}}! My name is \${{name}}."
        resolvable.rawExpressions.size shouldBe 1
        resolvable.rawExpressions[0].source shouldBe "name"
        resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{0}}."
        resolvable.resolveFormatting("Subjekt") shouldBe "Hello Subjekt! My name is Subjekt."
    }

    "A Resolvable should be built correctly when using custom expression delimiters" {
        val resolvable = Resolvable("Hello #name#! My name is #myName#.", "#", "#")
        resolvable.source shouldBe "Hello #name#! My name is #myName#."
        resolvable.rawExpressions.size shouldBe 2
        resolvable.rawExpressions[0].source shouldBe "name"
        resolvable.rawExpressions[1].source shouldBe "myName"
        resolvable.asFormattableString() shouldBe "Hello {{0}}! My name is {{1}}."
        resolvable.resolveFormatting("Subjekt", "Francesco") shouldBe "Hello Subjekt! My name is Francesco."
    }
})
