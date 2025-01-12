/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.visitors

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.nodes.suite.Template
import io.github.subjekt.utils.Expressions.evaluate
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.utils.MessageCollector.Message
import io.github.subjekt.utils.Permutations.permuteDefinitions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ExpressionEvaluationTest {
    private var context = Context.emptyContext()
    private val collector: MessageCollector = MessageCollector.SimpleCollector(showErrors = false)

    @BeforeEach
    fun setUp() {
        context = Context.of("a" to 1, "b" to 2)
        context.putMacro(Macro("foo", emptyList(), listOf(Template.parse("value1"), Template.parse("value2"))))
        // simply outputs the argument between parentheses () and {}
        context.putMacro(
            Macro(
                "bar",
                listOf("arg1"),
                listOf(Template.parse("(\${{ arg1 }})"), Template.parse("{\${{ arg1 }}}")),
            ),
        )
    }

    @AfterEach
    fun tearDown() {
        collector.showInConsole()
        collector.flushMessages()
    }

    fun evaluateMultiple(expr: String): List<String> {
        val code = "\${{ $expr }}"
        return Template
            .parse(code)
            .resolveCalls(context, collector)
            .permuteDefinitions()
            .fold(emptyList<String>()) { acc, calls ->
                context = context.withDefinedCalls(calls)
                acc + expr.evaluate(context, collector)
            }.filterNot(String::isBlank)
    }

    @Test
    fun `Trivial expression evaluation`() {
        val expr = "a + b"
        val result = expr.evaluate(context, collector)
        assertEquals("12", result)
    }

    @Test
    fun `Expression evaluation with literals`() {
        val expr = "\"a\" + \"b\""
        val result = expr.evaluate(context, collector)
        assertEquals("ab", result)
    }

    @Test
    fun `Expression evaluation with call`() {
        val expr = "foo()"
        val result = evaluateMultiple(expr)
        assertEquals(listOf("value1", "value2"), result)
    }

    @Test
    fun `Expression evaluation with call and argument`() {
        val expr = "bar(\"1\")"
        val result = evaluateMultiple(expr)
        assertEquals(listOf("(1)", "{1}"), result)
    }

//  @Test
//  fun `Expression evaluation with nested calls`() {
//    val expr = "bar(foo())"
//    val result = evaluateMultiple(expr)
//    assertEquals(
//      setOf(
//        "(value1)",
//        "{value1}",
//        "(value2)",
//        "{value2}",
//      ),
//      result.toSet(),
//    )
//  }

    @Test
    fun `Expression evaluation with nested calls and literals`() {
        val expr = "bar(\"1\" + \"2\")"
        val result = evaluateMultiple(expr)
        assertEquals(listOf("(12)", "{12}"), result)
    }

    @Test
    fun `Expression evaluation with nested calls and literals and arguments`() {
        val expr = "bar(\"1\" + \"2\" + a)"
        val result = evaluateMultiple(expr)
        assertEquals(listOf("(121)", "{121}"), result)
    }

//  @Test
//  fun `Expression evaluation with nested calls and literals and arguments and multiple calls`() {
//    val expr = "bar(\"1\" + \"2\" + a) + bar(\"3\" + \"4\" + b)"
//    val result = evaluateMultiple(expr)
//    assertEquals(setOf("(121)(342)", "(121){342}", "{121}(342)", "{121}{342}"), result.toSet())
//  }

    @Test
    fun `Expression with newline`() {
        context.putMacro(
            Macro(
                "aligned",
                listOf("code"),
                listOf(Template.parse("alignedOn(0) {\n\t\${{code }}\n}")),
            ),
        )
        val expr = "aligned(\"exampleCall(123)\")"
        val result = evaluateMultiple(expr)
        assertEquals(listOf("alignedOn(0) {\n\texampleCall(123)\n}"), result)
    }

    @Test
    fun `ID not defined`() {
        val expr = "c"
        val result = expr.evaluate(context, collector)
        assertEquals("", result)
        assertContains(
            collector.messages,
            Message(MessageCollector.MessageType.ERROR, "line 1: Identifier 'c' is not defined"),
        )
    }

    @Test
    fun `Macro not defined`() {
        val expr = "baz()"
        val result = evaluateMultiple(expr)
        assertEquals(emptyList(), result)
        assertContains(
            collector.messages,
            Message(MessageCollector.MessageType.ERROR, "line 1: Macro 'baz' is not defined"),
        )
    }

    @Test
    fun `Macro with wrong number of arguments`() {
        val expr = "bar()"
        val result = evaluateMultiple(expr)
        assertEquals(emptyList(), result)
        assertContains(
            collector.messages,
            Message(MessageCollector.MessageType.ERROR, "line 1: Macro 'bar' expects 1 arguments, but got 0"),
        )
    }
}
