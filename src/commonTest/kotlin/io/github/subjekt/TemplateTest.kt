/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.nodes.suite.Template
import io.github.subjekt.compiler.utils.MessageCollector
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TemplateTest : StringSpec({
    val messageCollector = MessageCollector.SimpleCollector()

    "Simple template parsing".config(enabled = false) {
        val templateString = "Hello, \${{ name }}!"
        val template = Template.parse(templateString)
        val expected = Template("Hello, %s!", listOf("name"), templateString)
        template shouldBe expected
    }

    "Simple template resolution".config(enabled = false) {
        val templateString = "Hello, \${{ name }}!"
        val template = Template.parse(templateString)
        val context = Context.of("name" to "World")
        val resolved = template.resolve(context, messageCollector)
        val expected = "Hello, World!"
        resolved shouldBe expected
    }

    "Multiple expressions template resolution".config(enabled = false) {
        val templateString = "Hello, \${{ name }}! I'm \${{ age }} years old."
        val template = Template.parse(templateString)
        val context = Context.of("name" to "World", "age" to 42)
        val resolved = template.resolve(context, messageCollector)
        val expected = "Hello, World! I'm 42 years old."
        resolved shouldBe expected
    }
})
