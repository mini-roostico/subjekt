/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */
@file:Suppress("TooManyFunctions")

package io.github.subjekt.engine.expressions.visitors

import io.github.subjekt.engine.expressions.Expression
import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.Error
import io.github.subjekt.engine.expressions.ir.IrAtomicNode
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrEndOfSlice
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrNativeType
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.IrParameter
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrTree
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.engine.expressions.ir.UnaryOperator
import io.github.subjekt.engine.expressions.ir.utils.IrUtils.binaryOperation
import io.github.subjekt.parsers.generated.ExpressionBaseVisitor
import io.github.subjekt.parsers.generated.ExpressionLexer
import io.github.subjekt.parsers.generated.ExpressionParser
import io.github.subjekt.utils.Utils.parsingFail
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ParserRuleContext

/**
 * Visitor that creates the intermediate representation of the expression (a tree of [IrNode]s).
 */
private class ExpressionIrCreationVisitor(
    /**
     * Expression body to parse into an IR tree.
     */
    val expressionSource: String,
    /**
     * Flag to enable IR tree logging.
     */
    private val enableLogging: Boolean = false,
    /**
     * Current indentation level for logging.
     */
    private var indentLevel: Int = 0,
) : ExpressionBaseVisitor<IrNode>() {
    /**
     * Current slice ID, used for slice expressions.
     */
    private var slice: String? = null

    private fun newSlice(
        id: String,
        creation: () -> IrNode,
    ): IrNode {
        slice = id
        val res = creation()
        slice = null
        return res
    }

    private fun log(message: String) {
        if (enableLogging) {
            val indent = "  ".repeat(indentLevel)
            println("$indent$message")
        }
    }

    private fun <T> withIndent(block: () -> T): T {
        indentLevel++
        return try {
            block()
        } finally {
            indentLevel--
        }
    }

    private fun logVisit(
        ruleName: String,
        context: ParserRuleContext,
        result: IrNode,
    ): IrNode {
        if (enableLogging) {
            log("Visiting $ruleName: '${context.text}' -> ${result::class.simpleName}")
        }
        return result
    }

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

    override fun visitMulDivMod(ctx: ExpressionParser.MulDivModContext): IrNode =
        withIndent {
            log("ModMulDiv: ${ctx.text}")
            val result =
                when {
                    ctx.mod != null ->
                        ctx.makeBinaryOperation(
                            ctx.left,
                            ctx.right,
                            BinaryOperator.MODULO,
                        )

                    ctx.mul != null ->
                        ctx.makeBinaryOperation(
                            ctx.left,
                            ctx.right,
                            BinaryOperator.MULTIPLY,
                        )

                    ctx.div != null ->
                        ctx.makeBinaryOperation(
                            ctx.left,
                            ctx.right,
                            BinaryOperator.DIVIDE,
                        )

                    else -> Error(0)
                }
            logVisit("ModMulDiv", ctx, result)
        }

    override fun visitQualifiedCall(ctx: ExpressionParser.QualifiedCallContext): IrNode =
        withIndent {
            log("DotCall: ${ctx.text}")
            val callId = ctx.call?.text ?: ""
            ctx.parsingCheck(callId.isNotBlank()) {
                "qualified call is missing identifiers"
            }
            val arguments = ctx.expr().map { visit(it) }
            val receiver = ctx.receiver ?: parsingFail { "Receiver of the dot call is missing" }
            val result =
                IrDotCall(
                    visit(receiver),
                    callId,
                    arguments.map { it },
                    ctx.start?.line ?: -1,
                )
            logVisit("DotCall", ctx, result)
        }

    override fun visitUnaryPlusMinus(ctx: ExpressionParser.UnaryPlusMinusContext): IrNode =
        withIndent {
            log("UnaryPlusMinus: ${ctx.text}")
            IrUnaryOperation(
                if (ctx.minus != null) UnaryOperator.MINUS else UnaryOperator.PLUS,
                visit(ctx.expr()),
                ctx.start?.line ?: -1,
            )
        }

    override fun visitAddSub(ctx: ExpressionParser.AddSubContext): IrNode =
        withIndent {
            log("PlusMinus: ${ctx.text}")
            val result =
                if (ctx.minus != null) {
                    ctx.makeBinaryOperation(
                        ctx.left,
                        ctx.right,
                        BinaryOperator.MINUS,
                    )
                } else {
                    ctx.makeBinaryOperation(
                        ctx.left,
                        ctx.right,
                        BinaryOperator.PLUS,
                    )
                }
            logVisit("PlusMinus", ctx, result)
        }

    override fun visitSlice(ctx: ExpressionParser.SliceContext): IrNode =
        withIndent {
            log("SliceExpr: ${ctx.text}")
            newSlice(ctx.ID().text) {
                visit(ctx.sliceExpr())
            }
        }

    override fun visitIdentifier(ctx: ExpressionParser.IdentifierContext): IrNode =
        withIndent {
            val result =
                IrParameter(
                    identifier = ctx.ID().text,
                    line = ctx.start?.line ?: -1,
                )
            logVisit("Identifier", ctx, result)
        }

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

    override fun visitConcat(ctx: ExpressionParser.ConcatContext): IrNode =
        withIndent {
            log("Concat: ${ctx.text}")
            val result =
                ctx.makeBinaryOperation(
                    ctx.left,
                    ctx.right,
                    BinaryOperator.CONCAT,
                )
            logVisit("Concat", ctx, result)
        }

    override fun visitIntLiteral(ctx: ExpressionParser.IntLiteralContext): IrNode =
        withIndent {
            val result =
                IrIntegerLiteral(
                    ctx
                        .INT()
                        .text
                        .replace("\\s".toRegex(), "")
                        .toIntOrNull()
                        ?: ctx.createError { "error converting ${ctx.text} to integer" },
                    ctx.start?.line ?: -1,
                )
            logVisit("IntLiteral", ctx, result)
        }

    override fun visitStringLiteral(ctx: ExpressionParser.StringLiteralContext): IrNode =
        withIndent {
            val result =
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
            logVisit("StringLiteral", ctx, result)
        }

    override fun visitFloatLiteral(ctx: ExpressionParser.FloatLiteralContext): IrNode =
        withIndent {
            val result =
                IrFloatLiteral(
                    ctx
                        .FLOAT()
                        .text
                        .replace("\\s".toRegex(), "")
                        .toDoubleOrNull()
                        ?: ctx.createError { "error converting ${ctx.text} to float" },
                    ctx.start?.line ?: -1,
                )
            logVisit("FloatLiteral", ctx, result)
        }

    override fun visitSingleSlice(ctx: ExpressionParser.SingleSliceContext): IrNode =
        withIndent {
            log("SingleSlice: ${ctx.text}")
            val result =
                IrSingleSlice(
                    identifier = slice ?: parsingFail { "Internal compiler error: slice ID was null" },
                    indexExpression = visit(ctx.index ?: ctx.createError { "error converting ${ctx.text} to index" }),
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SingleSlice", ctx, result)
        }

    override fun visitSliceStartStep(ctx: ExpressionParser.SliceStartStepContext): IrNode =
        withIndent {
            log("SliceStartStep: ${ctx.text}")
            val start = ctx.startSlice?.let { visit(it) } ?: ctx.createError { "start of slice is null" }
            val step = ctx.stepSlice?.let { visit(it) } ?: IrIntegerLiteral(1, ctx.start?.line ?: -1)
            val result =
                IrRangeSlice(
                    identifier = slice ?: ctx.createError { "slice ID is null" },
                    start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
                    step = step as? IrAtomicNode ?: ctx.createError { "step of slice is not atomic" },
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SliceStartStep", ctx, result)
        }

    override fun visitSliceEndStep(ctx: ExpressionParser.SliceEndStepContext): IrNode =
        withIndent {
            log("SliceEndStep: ${ctx.text}")
            val end = ctx.endSlice?.let { visit(it) } ?: IrEndOfSlice(ctx.start?.line ?: -1)
            val step = ctx.stepSlice?.let { visit(it) } ?: IrIntegerLiteral(1, ctx.start?.line ?: -1)
            val result =
                IrRangeSlice(
                    identifier = slice ?: ctx.createError { "slice ID is null" },
                    end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
                    step = step as? IrAtomicNode ?: ctx.createError { "step of slice is not atomic" },
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SliceEndStep", ctx, result)
        }

    override fun visitSliceStartEnd(ctx: ExpressionParser.SliceStartEndContext): IrNode =
        withIndent {
            log("SliceStartEnd: ${ctx.text}")
            val start = ctx.startSlice?.let { visit(it) } ?: IrIntegerLiteral(0, ctx.start?.line ?: -1)
            val end = ctx.endSlice?.let { visit(it) } ?: IrEndOfSlice(ctx.start?.line ?: -1)
            val result =
                IrRangeSlice(
                    identifier = slice ?: ctx.createError { "slice ID is null" },
                    start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
                    end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SliceStartEnd", ctx, result)
        }

    override fun visitSliceEnd(ctx: ExpressionParser.SliceEndContext): IrNode =
        withIndent {
            log("SliceEnd: ${ctx.text}")
            val end = ctx.endSlice?.let { visit(it) } ?: ctx.createError { "end of slice is null" }
            val result =
                IrRangeSlice(
                    identifier = slice ?: ctx.createError { "slice ID is null" },
                    end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SliceEnd", ctx, result)
        }

    override fun visitSliceStart(ctx: ExpressionParser.SliceStartContext): IrNode =
        withIndent {
            log("SliceStart: ${ctx.text}")
            val start = ctx.startSlice?.let { visit(it) } ?: ctx.createError { "start of slice is null" }
            val result =
                IrRangeSlice(
                    identifier = slice ?: ctx.createError { "slice ID is null" },
                    start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SliceStart", ctx, result)
        }

    override fun visitSliceWithStep(ctx: ExpressionParser.SliceWithStepContext): IrNode =
        withIndent {
            log("SliceWithStep: ${ctx.text}")
            val start = ctx.startSlice?.let { visit(it) } ?: IrIntegerLiteral(0, ctx.start?.line ?: -1)
            val end = ctx.endSlice?.let { visit(it) } ?: IrEndOfSlice(ctx.endSlice?.start?.line ?: -1)
            val step = ctx.stepSlice?.let { visit(it) } ?: IrIntegerLiteral(1, ctx.start?.line ?: -1)
            val result =
                IrRangeSlice(
                    identifier = slice ?: ctx.createError { "slice ID is null" },
                    start = start as? IrAtomicNode ?: ctx.createError { "start of slice is not atomic" },
                    end = end as? IrAtomicNode ?: ctx.createError { "end of slice is not atomic" },
                    step = step as? IrAtomicNode ?: ctx.createError { "step of slice is not atomic" },
                    line = ctx.start?.line ?: -1,
                )
            logVisit("SliceWithStep", ctx, result)
        }

    override fun visitCall(ctx: ExpressionParser.CallContext): IrNode =
        withIndent {
            log("MacroCall: ${ctx.text}")
            val id = ctx.ID().text
            ctx.parsingCheck(id.isNotBlank()) { "macro call has no identifier" }
            val arguments = ctx.expr().map { visit(it) }
            val result =
                IrCall(
                    id,
                    arguments.map { it },
                    ctx.start?.line ?: -1,
                )
            logVisit("MacroCall", ctx, result)
        }

    override fun visitIntCast(ctx: ExpressionParser.IntCastContext): IrNode =
        withIndent {
            log("IntCast: ${ctx.text}")
            val result =
                IrCast(
                    null,
                    IrNativeType.INTEGER,
                    ctx.start?.line ?: -1,
                )
            logVisit("IntCast", ctx, result)
        }

    override fun visitFloatCast(ctx: ExpressionParser.FloatCastContext): IrNode =
        withIndent {
            log("FloatCast: ${ctx.text}")
            val result =
                IrCast(
                    null,
                    IrNativeType.FLOAT,
                    ctx.start?.line ?: -1,
                )
            logVisit("FloatCast", ctx, result)
        }

    override fun visitStringCast(ctx: ExpressionParser.StringCastContext): IrNode =
        withIndent {
            log("StringCast: ${ctx.text}")
            val result =
                IrCast(
                    null,
                    IrNativeType.STRING,
                    ctx.start?.line ?: -1,
                )
            logVisit("StringCast", ctx, result)
        }

    override fun visitCast(ctx: ExpressionParser.CastContext): IrNode =
        withIndent {
            log("Cast: ${ctx.text}")
            (visit(ctx.castType()) as IrCast).apply { this.value = visit(ctx.expr()) }
        }

    override fun visitParenthesized(ctx: ExpressionParser.ParenthesizedContext): IrNode =
        withIndent {
            log("Parenthesized: ${ctx.text}")
            visit(ctx.expr())
        }

    override fun defaultResult(): IrNode = Error(0)
}

/**
 * Parses the expression into an IR tree.
 */
internal fun Expression.parseToIr(enableLogging: Boolean = false): IrTree {
    val charStream = CharStreams.fromString(source)
    val lexer = ExpressionLexer(charStream)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    val tree = parser.expr()

    if (enableLogging) {
        println("=== Parsing expression: '$source' ===")
    }

    val irTree = IrTree(ExpressionIrCreationVisitor(source, enableLogging).visit(tree))

    if (enableLogging) {
        println("=== IR Tree created successfully ===")
    }

    return irTree
}
