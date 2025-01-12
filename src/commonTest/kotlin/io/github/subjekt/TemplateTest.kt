/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.suite.Template
import io.github.subjekt.utils.MessageCollector
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateTest {
    val messageCollector = MessageCollector.NullCollector()

    @Test
    fun `Simple template parsing`() {
        val templateString = "Hello, \${{ name }}!"
        val template = Template.parse(templateString)
        val expected = Template("Hello, %s!", listOf("name"), templateString)
        assertEquals(expected, template)
    }

    @Test
    fun `Simple template resolution`() {
        val templateString = "Hello, \${{ name }}!"
        val template = Template.parse(templateString)
        val context = Context.of("name" to "World")
        val resolved = template.resolve(context, messageCollector)
        val expected = "Hello, World!"
        assertEquals(expected, resolved)
    }

    @Test
    fun `Multiple expressions template resolution`() {
        val templateString = "Hello, \${{ name }}! I'm \${{ age }} years old."
        val template = Template.parse(templateString)
        val context = Context.of("name" to "World", "age" to 42)
        val resolved = template.resolve(context, messageCollector)
        val expected = "Hello, World! I'm 42 years old."
        assertEquals(expected, resolved)
    }
}
