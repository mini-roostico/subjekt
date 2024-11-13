package io.github.subjekt

import io.github.subjekt.files.Macro
import io.github.subjekt.rendering.Rendering.renderVelocityMacroDeclarations
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MacroRenderingTest {

  @Test
  fun `Rendering a simple macro`() {
    val macro = Macro(
      "test",
      listOf("(1..3).forEach{ \$code }", "(1..3).map{ \$code }"),
      listOf("code")
    )
    val expectedDeclarations = listOf(
      "#macro(test \$code)\n" +
        " (1..3).forEach{ \$code }\n" +
      "#end",
      "#macro(test \$code)\n" +
        " (1..3).map{ \$code }\n" +
      "#end",
    )

    assertEquals(expectedDeclarations, macro.renderVelocityMacroDeclarations())
  }
}
