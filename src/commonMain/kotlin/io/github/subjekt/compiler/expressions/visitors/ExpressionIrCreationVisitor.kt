/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.visitors

import io.github.subjekt.compiler.expressions.Expression
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.nodes.expression.Node
import io.github.subjekt.parsers.generated.ExpressionBaseVisitor
import io.github.subjekt.parsers.generated.ExpressionLexer
import io.github.subjekt.parsers.generated.ExpressionParser
import io.github.subjekt.utils.Utils.parsingFail
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ParserRuleContext

/**
 * Visitor that creates the intermediate representation of the expression (a tree of [Node]s).
 */
internal class ExpressionIrCreationVisitor(
    /**
     * Expression body to parse into an IR tree.
     */
    val expressionSource: String,
) : ExpressionBaseVisitor<IrNode>() {
    private fun ParserRuleContext.createError(message: () -> String): Nothing {
        parsingFail {
            "Parsing expression $expressionSource failed: " +
                this.start
                    ?.line
                    ?.toString()
                    ?.plus(":")
                    ?.plus(
                        this.start
                            ?.charPositionInLine
                            ?.toString()
                            .orEmpty(),
                    ).orEmpty() +
                message()
        }
    }

    private fun ParserRuleContext.parsingCheck(
        check: Boolean,
        message: () -> String,
    ) {
        if (!check) {
            createError(message)
        }
    }

    override fun visitCall(ctx: ExpressionParser.CallContext): IrNode = visit(ctx.macroCall())

    override fun visitModuleCall(ctx: ExpressionParser.ModuleCallContext): IrNode = visit(ctx.dotCall())

    override fun visitVariable(ctx: ExpressionParser.VariableContext): IrNode =
        IrNode.IrParameter(ctx.text, ctx.start?.line ?: -1)

    override fun visitPlusExpr(ctx: ExpressionParser.PlusExprContext): IrNode {
        val left = ctx.expression(0)?.let { visit(it) } ?: ctx.createError { "left side of plus expression is null" }
        val right = ctx.expression(1)?.let { visit(it) } ?: ctx.createError { "right side of plus expression is null" }
        return IrNode.IrExpressionPlus(
            left,
            right,
            ctx.start?.line ?: -1,
        )
    }

    override fun visitLiteral(ctx: ExpressionParser.LiteralContext): IrNode =
        IrNode.IrLiteral(
            ctx.text
                .trim()
                .removePrefix("\"")
                .removePrefix("'")
                .removeSuffix("\"")
                .removeSuffix("'")
                .replace("\\\"", "\"")
                .replace("\\'", "'"),
            ctx.start?.line ?: -1,
        )

    override fun visitMacroCall(ctx: ExpressionParser.MacroCallContext): IrNode {
        val id = ctx.ID().text
        ctx.parsingCheck(id.isNotBlank()) { "macro call has no identifier" }
        val arguments = ctx.expression().map { visit(it) }
        return IrNode.IrCall(
            id,
            arguments.map { it },
            ctx.start?.line ?: -1,
        )
    }

    override fun visitDotCall(ctx: ExpressionParser.DotCallContext): IrNode {
        val moduleId = ctx.ID(0)?.text ?: ""
        val macroId = ctx.ID(1)?.text ?: ""
        ctx.parsingCheck(moduleId.isNotBlank() && macroId.isNotBlank()) { "module call is missing identifiers" }
        val arguments = ctx.expression().map { visit(it) }
        return IrNode.IrDotCall(
            moduleId,
            macroId,
            arguments.map { it },
            ctx.start?.line ?: -1,
        )
    }

    override fun defaultResult(): IrNode = IrNode.Error(0)
}

/**
 * Parses the expression into an IR tree.
 */
internal fun Expression.parseToIr(): IrNode {
    val charStream = CharStreams.fromString(source)
    val lexer = ExpressionLexer(charStream)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    val tree = parser.expression()
    return ExpressionIrCreationVisitor(source).visit(tree)
}
