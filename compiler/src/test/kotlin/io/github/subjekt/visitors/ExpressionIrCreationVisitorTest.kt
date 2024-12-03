package io.github.subjekt.visitors

import io.github.subjekt.ExpressionLexer
import io.github.subjekt.ExpressionParser
import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.expression.Node
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.utils.MessageCollector.Message
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExpressionIrCreationVisitorTest {

  private val collector: MessageCollector = MessageCollector.SimpleCollector(silent = true)

  fun String.visitExpression(): Node? {
    val stream = CharStreams.fromString(this)
    val lexer = ExpressionLexer(stream)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    val context = Context.emptyContext()
    collector.useParser(parser, context)
    val tree = parser.expression()
    return ExpressionIrCreationVisitor(context, collector).visit(tree)
  }

  @BeforeEach
  fun setUp() {
    collector.flushMessages()
  }

  @Test
  fun `Simple IR creation`() {
    val expr = "a + b"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Plus)
    val plusNode = node as Node.Plus
    assert(plusNode.left is Node.Id)
    assert(plusNode.right is Node.Id)
  }

  @Test
  fun `Simple IR creation with literals - double quoted`() {
    val expr = "\"a\" + \"b\""
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Plus)
    val plusNode = node as Node.Plus
    assert(plusNode.left is Node.Literal)
    assert(plusNode.right is Node.Literal)
  }

  @Test
  fun `Simple IR creation with literals - single quoted`() {
    val expr = "'a' + 'b'"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Plus)
    val plusNode = node as Node.Plus
    assert(plusNode.left is Node.Literal)
    assert(plusNode.right is Node.Literal)
  }

  @Test
  fun `Simple IR creation with literals containing escaped quotes`() {
    val expr = """"\"a\"" + '\"b\"'"""
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Plus)
    val plusNode = node as Node.Plus
    assert(plusNode.left is Node.Literal)
    assert(plusNode.right is Node.Literal)
    assertEquals("\"a\"", (plusNode.left as Node.Literal).value)
    assertEquals("\"b\"", (plusNode.right as Node.Literal).value)
  }

  @Test
  fun `Simple IR creation with call`() {
    val expr = "foo(a, b)"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Call)
    val callNode = node as Node.Call
    assert(callNode.arguments.size == 2)
    assert(callNode.arguments[0] is Node.Id)
    assert(callNode.arguments[1] is Node.Id)
  }

  @Test
  fun `Simple IR creation with nested calls`() {
    val expr = "foo(bar(a), b)"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Call)
    val callNode = node as Node.Call
    assert(callNode.arguments.size == 2)
    assert(callNode.arguments[0] is Node.Call)
    assert(callNode.arguments[1] is Node.Id)
  }

  @Test
  fun `Simple IR creation with nested calls and literals`() {
    val expr = "foo(bar(\"a\"), \"b\")"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Call)
    val callNode = node as Node.Call
    assert(callNode.arguments.size == 2)
    assert(callNode.arguments[0] is Node.Call)
    assert(callNode.arguments[1] is Node.Literal)
  }

  @Test
  fun `Simple IR creation with nested calls and plus`() {
    val expr = "foo(bar(a + b), c)"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Call)
    val callNode = node as Node.Call
    assert(callNode.arguments.size == 2)
    assert(callNode.arguments[0] is Node.Call)
    assert(callNode.arguments[1] is Node.Id)
  }

  @Test
  fun `Simple IR creation with nested calls and plus and literals`() {
    val expr = "foo(bar(\"a\" + \"b\"), \"c\")"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Call)
    val callNode = node as Node.Call
    assert(callNode.arguments.size == 2)
    assert(callNode.arguments[0] is Node.Call)
    assert(callNode.arguments[1] is Node.Literal)
  }

  @Test
  fun `Simple IR creation with nested calls and plus and literals and call`() {
    val expr = "foo(bar(\"a\" + \"b\"), baz(c))"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Call)
    val callNode = node as Node.Call
    assert(callNode.arguments.size == 2)
    assert(callNode.arguments[0] is Node.Call)
    assert(callNode.arguments[1] is Node.Call)
  }

  @Test
  fun `Complex IR creation`() {
    val expr = "foo(bar(a + b), baz(c + d)) + \"e\""
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.Plus)
    val plusNode = node as Node.Plus
    assert(plusNode.left is Node.Call)
    assert(plusNode.right is Node.Literal)
    assert((plusNode.left as Node.Call).arguments[0] is Node.Call)
    assert(plusNode.left.arguments[1] is Node.Call)
    assert((plusNode.left.arguments[0] as Node.Call).arguments[0] is Node.Plus)
    assert((plusNode.left.arguments[1] as Node.Call).arguments[0] is Node.Plus)
  }

  @Test
  fun `Simple dot call`() {
    val expr = "foo.bar()"
    val node = expr.visitExpression()
    assert(collector.messages.isEmpty())
    assert(node is Node.DotCall)
  }

  @Test
  fun `Plus syntax error`() {
    val expr = "a +"
    val node = expr.visitExpression()
    assertNull(node)
    assert(collector.messages.isNotEmpty())
    assertContains(
      collector.messages,
      Message(MessageCollector.MessageType.ERROR, "line 1:3: mismatched input '<EOF>' expecting {STRING, ID}"),
    )
  }

  @Test
  fun `Call syntax error`() {
    val expr = "foo(a"
    expr.visitExpression()
    assert(collector.messages.isNotEmpty())
    assertContains(
      collector.messages,
      Message(MessageCollector.MessageType.ERROR, "line 1:5: mismatched input '<EOF>' expecting {',', ')'}"),
    )
  }
}
