/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.visitors

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.nodes.suite.Macro
import io.github.subjekt.compiler.nodes.suite.Template
import io.github.subjekt.compiler.utils.Expressions.evaluate
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.compiler.utils.Permutations.permuteDefinitions
import io.github.subjekt.utils.MessageCollector.Message
import io.github.subjekt.utils.MessageCollector.MessageType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class ExpressionEvaluationTest : StringSpec({
    var context = Context.emptyContext()
    val collector: MessageCollector = MessageCollector.SimpleCollector(showErrors = false)

    beforeTest {
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

    afterTest {
        // collector.showInConsole()
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

    "Trivial expression evaluation".config(enabled = false) {
        val expr = "a + b"
        val result = expr.evaluate(context, collector)
        result shouldBe "12"
    }

    "Expression evaluation with literals".config(enabled = false) {
        val expr = "\"a\" + \"b\""
        val result = expr.evaluate(context, collector)
        result shouldBe "ab"
    }

    "Expression evaluation with call".config(enabled = false) {
        val expr = "foo()"
        val result = evaluateMultiple(expr)
        result shouldBe listOf("value1", "value2")
    }

    "Expression evaluation with call and argument".config(enabled = false) {
        val expr = "bar(\"1\")"
        val result = evaluateMultiple(expr)
        result shouldBe listOf("(1)", "{1}")
    }

    "Expression evaluation with nested calls and literals".config(enabled = false) {
        val expr = "bar(\"1\" + \"2\")"
        val result = evaluateMultiple(expr)
        result shouldBe listOf("(12)", "{12}")
    }

    "Expression evaluation with nested calls and literals and arguments".config(enabled = false) {
        val expr = "bar(\"1\" + \"2\" + a)"
        val result = evaluateMultiple(expr)
        result shouldBe listOf("(121)", "{121}")
    }

    "Expression with newline".config(enabled = false) {
        context.putMacro(
            Macro(
                "aligned",
                listOf("code"),
                listOf(Template.parse("alignedOn(0) {\n\t\${{code }}\n}")),
            ),
        )
        val expr = "aligned(\"exampleCall(123)\")"
        val result = evaluateMultiple(expr)
        result shouldBe listOf("alignedOn(0) {\n\texampleCall(123)\n}")
    }

    "ID not defined".config(enabled = false) {
        val expr = "c"
        val result = expr.evaluate(context, collector)
        result shouldBe ""
        collector.messages shouldContain
            Message(
                MessageType.ERROR,
                "line 1: Identifier 'c' is not defined",
            )
    }

    "Macro not defined".config(enabled = false) {
        val expr = "baz()"
        val result = evaluateMultiple(expr)
        result.shouldBeEmpty()
        collector.messages shouldContain
            Message(
                MessageType.ERROR,
                "line 1: Macro 'baz' is not defined",
            )
    }

    "Macro with wrong number of arguments".config(enabled = false) {
        val expr = "bar()"
        val result = evaluateMultiple(expr)
        result.shouldBeEmpty()
        collector.messages shouldContain
            Message(
                MessageType.ERROR,
                "line 1: Macro 'bar' expects 1 arguments, but got 0",
            )
    }
})
