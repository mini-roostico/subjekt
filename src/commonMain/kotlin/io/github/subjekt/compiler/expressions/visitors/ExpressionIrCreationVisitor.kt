/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */
@file:Suppress("TooManyFunctions")

package io.github.subjekt.compiler.expressions.visitors

import io.github.subjekt.compiler.expressions.Expression
import io.github.subjekt.compiler.expressions.ir.BinaryOperator
import io.github.subjekt.compiler.expressions.ir.Error
import io.github.subjekt.compiler.expressions.ir.IrAtomicNode
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrCast
import io.github.subjekt.compiler.expressions.ir.IrCompleteSlice
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrEndOfSlice
import io.github.subjekt.compiler.expressions.ir.IrEndSlice
import io.github.subjekt.compiler.expressions.ir.IrFloatLiteral
import io.github.subjekt.compiler.expressions.ir.IrIntegerLiteral
import io.github.subjekt.compiler.expressions.ir.IrNativeType
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.expressions.ir.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrSingleSlice
import io.github.subjekt.compiler.expressions.ir.IrStartEndSlice
import io.github.subjekt.compiler.expressions.ir.IrStartSlice
import io.github.subjekt.compiler.expressions.ir.IrStringLiteral
import io.github.subjekt.compiler.expressions.ir.IrTree
import io.github.subjekt.compiler.expressions.ir.utils.IrUtils.binaryOperation
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
private class ExpressionIrCreationVisitor(
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
                    ).orEmpty() + " " +
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

    override fun visitIdentifier(ctx: ExpressionParser.IdentifierContext): IrNode =
        IrParameter(
            identifier = ctx.ID().text,
            line = ctx.start?.line ?: -1,
        )

    override fun visitSingleSliceExpr(ctx: ExpressionParser.SingleSliceExprContext): IrNode = visit(ctx.singleSlice())

    private fun ParserRuleContext.makeBinaryOperation(
        left: ParserRuleContext?,
        right: ParserRuleContext?,
        op: BinaryOperator,
    ): IrAtomicNode =
        left.binaryOperation(
            right,
            op,
            start?.line ?: -1,
            {
                createError { "$it side of integer ${op.toString().lowercase()} expression is null" }
            },
            { visit(it) },
        )

    override fun visitPlusMinus(ctx: ExpressionParser.PlusMinusContext): IrNode =
        if (ctx.minus != null) {
            ctx.makeBinaryOperation(
                ctx.atomicExpr(0),
                ctx.atomicExpr(1),
                BinaryOperator.MINUS,
            )
        } else {
            ctx.makeBinaryOperation(
                ctx.atomicExpr(0),
                ctx.atomicExpr(1),
                BinaryOperator.PLUS,
            )
        }

    override fun visitModMulDiv(ctx: ExpressionParser.ModMulDivContext): IrNode =
        when {
            ctx.mod != null ->
                ctx.makeBinaryOperation(
                    ctx.atomicExpr(0),
                    ctx.atomicExpr(1),
                    BinaryOperator.MODULO,
                )
            ctx.mul != null ->
                ctx.makeBinaryOperation(
                    ctx.atomicExpr(0),
                    ctx.atomicExpr(1),
                    BinaryOperator.MULTIPLY,
                )
            ctx.div != null ->
                ctx.makeBinaryOperation(
                    ctx.atomicExpr(0),
                    ctx.atomicExpr(1),
                    BinaryOperator.DIVIDE,
                )
            else -> Error(0)
        }

    override fun visitAtomicParenthesis(ctx: ExpressionParser.AtomicParenthesisContext): IrNode =
        visit(ctx.atomicExpr())

    override fun visitConcat(ctx: ExpressionParser.ConcatContext): IrNode =
        ctx.makeBinaryOperation(
            ctx.atomicExpr(0),
            ctx.atomicExpr(1),
            BinaryOperator.CONCAT,
        )

    override fun visitIntLiteral(ctx: ExpressionParser.IntLiteralContext): IrNode =
        IrIntegerLiteral(
            ctx.NUMBER().text.toIntOrNull()
                ?: ctx.createError { "error converting ${ctx.text} to integer" },
            ctx.start?.line ?: -1,
        )

    override fun visitStringLiteral(ctx: ExpressionParser.StringLiteralContext): IrNode =
        IrStringLiteral(
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

    override fun visitFloatLiteral(ctx: ExpressionParser.FloatLiteralContext): IrNode =
        IrFloatLiteral(
            ctx.FLOAT().text.toDoubleOrNull()
                ?: ctx.createError { "error converting ${ctx.text} to float" },
            ctx.start?.line ?: -1,
        )

    override fun visitSingleSlice(ctx: ExpressionParser.SingleSliceContext): IrNode =
        IrSingleSlice(
            identifier = ctx.ID().text,
            indexExpression = visit(ctx.atomicExpr()),
            line = ctx.start?.line ?: -1,
        )

    override fun visitSliceStartEnd(ctx: ExpressionParser.SliceStartEndContext): IrNode {
        val start = ctx.startExpr?.let { visit(it) } ?: ctx.createError { "start of slice is null" }
        val end = ctx.endExpr?.let { visit(it) } ?: ctx.createError { "end of slice is null" }
        return IrStartEndSlice(
            identifier = ctx.ID().text,
            start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
            end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
            line = ctx.start?.line ?: -1,
        )
    }

    override fun visitSliceEnd(ctx: ExpressionParser.SliceEndContext): IrNode {
        val end = ctx.endExpr?.let { visit(it) } ?: ctx.createError { "end of slice is null" }
        return IrEndSlice(
            identifier = ctx.ID().text,
            end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
            line = ctx.start?.line ?: -1,
        )
    }

    override fun visitSliceStart(ctx: ExpressionParser.SliceStartContext): IrNode {
        val start = ctx.startExpr?.let { visit(it) } ?: ctx.createError { "start of slice is null" }
        return IrStartSlice(
            identifier = ctx.ID().text,
            start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
            line = ctx.start?.line ?: -1,
        )
    }

    override fun visitSliceWithStep(ctx: ExpressionParser.SliceWithStepContext): IrNode {
        val start = ctx.startExpr?.let { visit(it) } ?: IrIntegerLiteral(0, ctx.start?.line ?: -1)
        val end = ctx.endExpr?.let { visit(it) } ?: IrEndOfSlice(ctx.start?.line ?: -1)
        val step = ctx.stepExpr?.let { visit(it) } ?: IrIntegerLiteral(1, ctx.start?.line ?: -1)
        return IrCompleteSlice(
            identifier = ctx.ID().text,
            start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
            end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
            step = step as? IrAtomicNode ?: ctx.createError { "step of slice is not atomic" },
            line = ctx.start?.line ?: -1,
        )
    }

    override fun visitCall(ctx: ExpressionParser.CallContext): IrNode = visit(ctx.macroCall())

    override fun visitModuleCall(ctx: ExpressionParser.ModuleCallContext): IrNode = visit(ctx.dotCall())

    override fun visitMacroCall(ctx: ExpressionParser.MacroCallContext): IrNode {
        val id = ctx.ID().text
        ctx.parsingCheck(id.isNotBlank()) { "macro call has no identifier" }
        val arguments = ctx.expression().map { visit(it) }
        return IrCall(
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
        return IrDotCall(
            moduleId,
            macroId,
            arguments.map { it },
            ctx.start?.line ?: -1,
        )
    }

    override fun visitIntCast(ctx: ExpressionParser.IntCastContext): IrNode =
        IrCast(
            visit(ctx.atomicExpr()),
            IrNativeType.INTEGER,
            ctx.start?.line ?: -1,
        )

    override fun visitFloatCast(ctx: ExpressionParser.FloatCastContext): IrNode =
        IrCast(
            visit(ctx.atomicExpr()),
            IrNativeType.FLOAT,
            ctx.start?.line ?: -1,
        )

    override fun visitStringCast(ctx: ExpressionParser.StringCastContext): IrNode =
        IrCast(
            visit(ctx.atomicExpr()),
            IrNativeType.STRING,
            ctx.start?.line ?: -1,
        )

    override fun visitSliceExpr(ctx: ExpressionParser.SliceExprContext): IrNode = visit(ctx.rangeSlice())

    override fun visitParenthesis(ctx: ExpressionParser.ParenthesisContext): IrNode = visit(ctx.expression())

    override fun visitResolvable(ctx: ExpressionParser.ResolvableContext): IrNode = visit(ctx.resolvableExpr())

    override fun visitAtomic(ctx: ExpressionParser.AtomicContext): IrNode = visit(ctx.atomicExpr())

    override fun visitCast(ctx: ExpressionParser.CastContext): IrNode = visit(ctx.castExpr())

    override fun defaultResult(): IrNode = Error(0)
}

/**
 * Parses the expression into an IR tree.
 */
internal fun Expression.parseToIr(): IrTree {
    val charStream = CharStreams.fromString(source)
    val lexer = ExpressionLexer(charStream)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    val tree = parser.expression()
    return IrTree(ExpressionIrCreationVisitor(source).visit(tree))
}
