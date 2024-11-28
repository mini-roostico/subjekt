package io.github.subjekt

import io.github.subjekt.Subjekt.asCode
import io.github.subjekt.files.Macro
import io.github.subjekt.files.Parameter
import io.github.subjekt.files.Subject
import io.github.subjekt.files.Suite
import io.github.subjekt.rendering.EngineProvider
import io.github.subjekt.rendering.Rendering
import io.github.subjekt.rendering.engines.VelocityEngine
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SuiteRenderingTest {

  @BeforeEach
  fun setUp() {
    EngineProvider.register("velocity", VelocityEngine())
  }

  @Test
  fun `Rendering a simple suite`() {
    val suite = Suite(
      "Example",
      listOf(
        Subject("It1", null, "println(\$PAR)", emptyList()),
        Subject("It2", null, "log(\$PAR)", emptyList())
      ),
      null,
      listOf(Parameter("PAR", listOf(5, 10)))
    )
    val expected = listOf(
      setOf("println(5)", "println(10)"),
      setOf("log(5)", "log(10)")
    )
    with(Rendering()) {
      assertEquals(expected, suite.resolve().asCode())
    }
  }

  @Test
  fun `Rendering a suite with macros and parameters`() {
    val suite = Suite(
      "Example",
      listOf(
        Subject("It1", null, "println(#M1(\$PAR))", emptyList()),
        Subject("It2", null, "log(#M1(\$PAR))", emptyList())
      ),
      listOf(
        Macro(
          "M1",
          listOf("pretty(\$arg)", "ugly(\$arg)"),
          listOf("arg")
        )
      ),
      listOf(Parameter("PAR", listOf(5, 10)))
    )
    val expected = listOf(
      setOf("println(pretty(5))", "println(ugly(5))", "println(pretty(10))", "println(ugly(10))"),
      setOf("log(pretty(5))", "log(ugly(5))", "log(pretty(10))", "log(ugly(10))"),
    )
    with(Rendering()) {
      assertEquals(expected, suite.resolve().asCode())
    }
  }

  @Test
  fun `Rendering a suite with macros and parameters also in subjects`() {
    val suite = Suite(
      "Example",
      listOf(
        Subject(
          "It1", listOf(
            Parameter("SUBPAR", listOf("true", "false"))
          ), "println(#M1(\$PAR), \$SUBPAR)", emptyList()
        ),
        Subject("It2", null, "log(#M1(\$PAR))", emptyList())
      ),
      listOf(
        Macro(
          "M1",
          listOf("pretty(\$arg)", "ugly(\$arg)"),
          listOf("arg")
        )
      ),
      listOf(Parameter("PAR", listOf(5, 10)))
    )
    val expected = listOf(
      setOf(
        "println(pretty(5), true)", "println(ugly(5), true)", "println(pretty(10), true)", "println(ugly(10), true)",
        "println(pretty(5), false)", "println(ugly(5), false)", "println(pretty(10), false)", "println(ugly(10), false)"
      ),
      setOf("log(pretty(5))", "log(ugly(5))", "log(pretty(10))", "log(ugly(10))"),
    )
    with(Rendering()) {
      assertEquals(expected, suite.resolve().asCode())
    }
  }

  @Test
  fun `Rendering a suite with macros and parameters also in subjects with overlapping names`() {
    val suite = Suite(
      "Example",
      listOf(
        Subject(
          "It1", listOf(
            Parameter("SUBPAR", listOf("true", "false")),
             Parameter("PAR", listOf("10"))
          ), "println(#M1(\$PAR), \$SUBPAR)", emptyList()
        ),
        Subject("It2", null, "log(#M1(\$PAR))", emptyList())
      ),
      listOf(
        Macro(
          "M1",
          listOf("pretty(\$arg)", "ugly(\$arg)"),
          listOf("arg")
        )
      ),
      listOf(Parameter("PAR", listOf(5)))
    )
    val expected = listOf(
      setOf(
        "println(pretty(5), true)", "println(ugly(5), true)", "println(pretty(10), true)", "println(ugly(10), true)",
        "println(pretty(5), false)", "println(ugly(5), false)", "println(pretty(10), false)", "println(ugly(10), false)"
      ),
      setOf("log(pretty(5))", "log(ugly(5))"),
    )
    with(Rendering()) {
      assertEquals(expected, suite.resolve().asCode())
    }
  }

  @Test
  fun `Empty parameters and Macros`() {
    val suite = Suite(
      "Example",
      listOf(
        Subject("It1", null, "println(5)", emptyList()),
        Subject("It2", null, "println(10)", emptyList()),
      ),
      null,
      null
    )
    val expected = listOf(
      setOf("println(5)"),
      setOf("println(10)"),
    )
    with(Rendering()) {
      assertEquals(expected, suite.resolve().asCode())
    }
  }
}
