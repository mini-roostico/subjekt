/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.visitors

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.nodes.expression.Node
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.parsers.generated.ExpressionBaseVisitor
import io.github.subjekt.parsers.generated.ExpressionParser
import io.github.subjekt.utils.MessageCollector.Position
import org.antlr.v4.kotlinruntime.ParserRuleContext

/**
 * Visitor that creates the intermediate representation of the expression (a tree of [Node]s).
 */
class ExpressionIrCreationVisitor(
    /**
     * Starting context for the expression.
     */
    val context: Context,
    /**
     * Message collector to report errors.
     */
    val messageCollector: MessageCollector,
) : ExpressionBaseVisitor<Node?>() {
    private fun ParserRuleContext.createError(message: String) {
        messageCollector.error(message, context, Position(this.start!!.line, this.start!!.charPositionInLine))
    }

    override fun visitCall(ctx: ExpressionParser.CallContext): Node? = visit(ctx.macroCall())

    override fun visitModuleCall(ctx: ExpressionParser.ModuleCallContext): Node? = visit(ctx.dotCall())

    override fun visitVariable(ctx: ExpressionParser.VariableContext): Node? = Node.Id(ctx.text, ctx.start!!.line)

    override fun visitPlusExpr(ctx: ExpressionParser.PlusExprContext): Node? {
        val left = visit(ctx.expression(0)!!)
        val right = visit(ctx.expression(1)!!)
        if (left == null) {
            ctx.createError("Left side of plus expression is null")
            return null
        }
        if (right == null) {
            ctx.createError("right side of plus expression is null")
            return null
        }
        return Node.Plus(
            visit(ctx.expression(0)!!)!!,
            visit(ctx.expression(1)!!)!!,
            ctx.start!!.line,
        )
    }

    override fun visitLiteral(ctx: ExpressionParser.LiteralContext): Node? =
        Node.Literal(
            ctx.text
                .trim()
                .removePrefix("\"")
                .removePrefix("'")
                .removeSuffix("\"")
                .removeSuffix("'")
                .replace("\\\"", "\"")
                .replace("\\'", "'"),
            ctx.start!!.line,
        )

    override fun visitMacroCall(ctx: ExpressionParser.MacroCallContext): Node? {
        val id = ctx.ID().text
        if (id.isBlank()) {
            ctx.createError("macro call has no identifier")
            return null
        }
        val arguments = ctx.expression().map { visit(it) }
        return Node.Call(
            id,
            arguments.map { it!! }, // todo fix unsafe
            ctx.start!!.line,
        )
    }

    override fun visitDotCall(ctx: ExpressionParser.DotCallContext): Node? {
        val moduleId = ctx.ID(0)?.text
        val macroId = ctx.ID(1)?.text
        if (moduleId == null || macroId == null) {
            ctx.createError("module call has no identifier")
            return null
        }
        val arguments = ctx.expression().map { visit(it) }
        return Node.DotCall(
            moduleId,
            macroId,
            arguments.map { it!! }, // todo fix unsafe
            ctx.start!!.line,
        )
    }

    override fun defaultResult(): Node = Node.Error(-1)
}
