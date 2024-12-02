package io.github.subjekt.visitors

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.nodes.suite.Template
import io.github.subjekt.utils.Expressions.evaluate
import io.github.subjekt.utils.MessageCollector
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionEvaluationTest {

  private var context = Context.emptyContext()
  private val collector: MessageCollector = MessageCollector.SimpleCollector()

  @BeforeEach
  fun setUp() {
    context = Context.of("a" to 1, "b" to 2)
    context.putMacro(Macro("foo", emptyList(), listOf(Template.parse("value1"), Template.parse("value2"))))
    // simply outputs the argument between parentheses () and {}
    context.putMacro(
      Macro(
        "bar",
        listOf("arg1"),
        listOf(Template.parse("(\${{ arg1 }})"), Template.parse("{\${{ arg1 }}}"))
      )
    )
  }

  @AfterEach
  fun tearDown() {
    collector.showInConsole()
    collector.flushMessages()
  }

  @Test
  fun `Trivial expression evaluation`() {
    val expr = "a + b"
    val result = expr.evaluate(context, collector)
    assertEquals(listOf("12"), result)
  }

  @Test
  fun `Expression evaluation with literals`() {
    val expr = "\"a\" + \"b\""
    val result = expr.evaluate(context, collector)
    assertEquals(listOf("ab"), result)
  }

  @Test
  fun `Expression evaluation with call`() {
    val expr = "foo()"
    val result = expr.evaluate(context, collector)
    assertEquals(listOf("value1", "value2"), result)
  }

  @Test
  fun `Expression evaluation with call and argument`() {
    val expr = "bar(\"1\")"
    val result = expr.evaluate(context, collector)
    assertEquals(listOf("(1)", "{1}"), result)
  }

  @Test
  fun `Expression evaluation with nested calls`() {
    val expr = "bar(bar(foo()))"
    val result = expr.evaluate(context, collector)
    assertEquals(
      setOf(
        "((value1))", "({value1})", "{(value1)}", "{{value1}}",
        "((value2))", "({value2})", "{(value2)}", "{{value2}}"
      ), result.toSet()
    )
  }

}
