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
import io.github.subjekt.compiler.nodes.expression.Node
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.compiler.utils.MessageCollector.Message
import io.github.subjekt.compiler.visitors.ExpressionIrCreationVisitor
import io.github.subjekt.parsers.generated.ExpressionLexer
import io.github.subjekt.parsers.generated.ExpressionParser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream

class ExpressionIrCreationVisitorTest : StringSpec({
    val collector: MessageCollector = MessageCollector.SimpleCollector(showErrors = false)

    fun String.visitExpression(): Node? {
        val stream = CharStreams.fromString(this)
        val lexer = ExpressionLexer(stream)
        val tokens = CommonTokenStream(lexer)
        val parser = ExpressionParser(tokens)
        val context = Context.emptyContext()
        collector.setLexerAndParser(lexer, parser, context)
        val tree = parser.expression()
        return ExpressionIrCreationVisitor(context, collector).visit(tree)
    }

    beforeTest {
        collector.flushMessages()
    }

    "Simple IR creation" {
        val expr = "a + b"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Plus::class
        val plusNode = node as Node.Plus
        plusNode.left shouldBe Node.Id::class
        plusNode.right shouldBe Node.Id::class
    }

    "Simple IR creation with literals - double quoted" {
        val expr = "\"a\" + \"b\""
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Plus::class
        val plusNode = node as Node.Plus
        plusNode.left shouldBe Node.Literal::class
        plusNode.right shouldBe Node.Literal::class
    }

    "Simple IR creation with literals - single quoted" {
        val expr = "'a' + 'b'"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Plus::class
        val plusNode = node as Node.Plus
        plusNode.left shouldBe Node.Literal::class
        plusNode.right shouldBe Node.Literal::class
    }

    "Simple IR creation with literals containing escaped quotes" {
        val expr = """"\"a\"" + '\"b\"'"""
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Plus::class
        val plusNode = node as Node.Plus
        plusNode.left shouldBe Node.Literal::class
        plusNode.right shouldBe Node.Literal::class
        (plusNode.left as Node.Literal).value shouldBe "\"a\""
        (plusNode.right as Node.Literal).value shouldBe "\"b\""
    }

    "Simple IR creation with call" {
        val expr = "foo(a, b)"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Call::class
        val callNode = node as Node.Call
        callNode.arguments.size shouldBe 2
        callNode.arguments[0] shouldBe Node.Id::class
        callNode.arguments[1] shouldBe Node.Id::class
    }

    "Simple IR creation with nested calls" {
        val expr = "foo(bar(a), b)"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Call::class
        val callNode = node as Node.Call
        callNode.arguments.size shouldBe 2
        callNode.arguments[0] shouldBe Node.Call::class
        callNode.arguments[1] shouldBe Node.Id::class
    }

    "Simple IR creation with nested calls and literals" {
        val expr = "foo(bar(\"a\"), \"b\")"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Call::class
        val callNode = node as Node.Call
        callNode.arguments.size shouldBe 2
        callNode.arguments[0] shouldBe Node.Call::class
        callNode.arguments[1] shouldBe Node.Literal::class
    }

    "Simple IR creation with nested calls and plus" {
        val expr = "foo(bar(a + b), c)"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Call::class
        val callNode = node as Node.Call
        callNode.arguments.size shouldBe 2
        callNode.arguments[0] shouldBe Node.Call::class
        callNode.arguments[1] shouldBe Node.Id::class
    }

    "Simple IR creation with nested calls and plus and literals" {
        val expr = "foo(bar(\"a\" + \"b\"), \"c\")"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Call::class
        val callNode = node as Node.Call
        callNode.arguments.size shouldBe 2
        callNode.arguments[0] shouldBe Node.Call::class
        callNode.arguments[1] shouldBe Node.Literal::class
    }

    "Simple IR creation with nested calls and plus and literals and call" {
        val expr = "foo(bar(\"a\" + \"b\"), baz(c))"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Call::class
        val callNode = node as Node.Call
        callNode.arguments.size shouldBe 2
        callNode.arguments[0] shouldBe Node.Call::class
        callNode.arguments[1] shouldBe Node.Call::class
    }

    "Complex IR creation" {
        val expr = "foo(bar(a + b), baz(c + d)) + \"e\""
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.Plus::class
        val plusNode = node as Node.Plus
        plusNode.left shouldBe Node.Call::class
        plusNode.right shouldBe Node.Literal::class
        (plusNode.left as Node.Call).arguments[0] shouldBe Node.Call::class
        plusNode.left.arguments[1] shouldBe Node.Call::class
        (plusNode.left.arguments[0] as Node.Call).arguments[0] shouldBe Node.Plus::class
        (plusNode.left.arguments[1] as Node.Call).arguments[0] shouldBe Node.Plus::class
    }

    "Simple dot call" {
        val expr = "foo.bar()"
        val node = expr.visitExpression()
        collector.messages.shouldBe(emptyList())
        node shouldBe Node.DotCall::class
    }

    "Plus syntax error" {
        val expr = "a +"
        val node = expr.visitExpression()
        node shouldBe null
        collector.messages shouldContain
            Message(
                MessageCollector.MessageType.ERROR,
                "line 1:3: mismatched input '<EOF>' expecting {STRING, ID}",
            )
    }

    "Call syntax error" {
        val expr = "foo(a"
        expr.visitExpression()
        collector.messages shouldContain
            Message(MessageCollector.MessageType.ERROR, "line 1:5: mismatched input '<EOF>' expecting {',', ')'}")
    }
})
