/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.utils

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.nodes.suite.Subject
import io.github.subjekt.compiler.resolved.DefinedCall
import io.github.subjekt.compiler.visitors.ExpressionIrCreationVisitor
import io.github.subjekt.compiler.visitors.ExpressionIrVisitor
import io.github.subjekt.compiler.visitors.ExpressionResolveVisitor
import io.github.subjekt.parsers.generated.ExpressionLexer
import io.github.subjekt.parsers.generated.ExpressionParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream

/**
 * Utility object for expressions.
 */
object Expressions {
    /**
     * Evaluates the receiver [String] as a Subjekt expression, using the provided [context] to resolve parameters and
     * macros' calls. The [messageCollector] is used to report any error that occurs during the evaluation.
     *
     * Returns a list of strings representing the possible results of the expression. It internally uses the visitors.
     */
    fun String.evaluate(
        context: Context,
        messageCollector: MessageCollector,
    ): String =
        this.acceptExpressionVisitor(
            ExpressionResolveVisitor(context, messageCollector),
            context,
            messageCollector,
            "",
        )

    /**
     * Accepts the receiver [String] as a Subjekt expression and visits it with the provided [visitor]. The [context] is
     * the one used to resolve parameters and macros' calls. The [messageCollector] is used to report any error that occurs.
     * The [defaultValueIfError] is the value that will be returned if an error occurs during the evaluation.
     *
     * Returns the result of the given [visitor].
     */
    fun <T> String.acceptExpressionVisitor(
        visitor: ExpressionIrVisitor<T>,
        context: Context,
        messageCollector: MessageCollector,
        defaultValueIfError: T,
    ): T {
        val stream = CharStreams.fromString(this)
        val lexer = ExpressionLexer(stream)
        messageCollector.useLexer(lexer, context)
        val tokens = CommonTokenStream(lexer)
        val parser = ExpressionParser(tokens)
        messageCollector.useParser(parser, context)
        val tree = parser.expression()
        if (parser.numberOfSyntaxErrors > 0) {
            return defaultValueIfError
        }
        val ast = ExpressionIrCreationVisitor(context, messageCollector).visit(tree)!!
        return visitor.visit(ast)
    }

    fun Subject.resolveCalls(
        context: Context,
        messageCollector: MessageCollector,
    ): Iterable<DefinedCall> =
        this.name.resolveCalls(context, messageCollector) + this.code.resolveCalls(context, messageCollector) +
            this.outcomes.flatMap { it.message.resolveCalls(context, messageCollector) }
}
