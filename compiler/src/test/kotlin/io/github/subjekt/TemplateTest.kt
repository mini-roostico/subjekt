package io.github.subjekt

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.suite.Template
import kotlin.test.Test
import kotlin.test.assertEquals

class TemplateTest {

  @Test
  fun `Simple template parsing`() {
    val templateString = "Hello, \${{ name }}!"
    val template = Template.parse(templateString)
    val expected = Template("Hello, %s!", listOf("name"))
    assertEquals(expected, template)
  }

  @Test
  fun `Simple template resolution`() {
    val templateString = "Hello, \${{ name }}!"
    val template = Template.parse(templateString)
    val context = Context.of("name" to "World")
    val resolved = template.resolve(context)
    val expected = listOf("Hello, World!")
    assertEquals(expected, resolved)
  }

  @Test
  fun `Multiple expressions template resolution`() {
    val templateString = "Hello, \${{ name }}! I'm \${{ age }} years old."
    val template = Template.parse(templateString)
    val context = Context.of("name" to "World", "age" to 42)
    val resolved = template.resolve(context)
    val expected = listOf("Hello, World! I'm 42 years old.")
    assertEquals(expected, resolved)
  }
}
