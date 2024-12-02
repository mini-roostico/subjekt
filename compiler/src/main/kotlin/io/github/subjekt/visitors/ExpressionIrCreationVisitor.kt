package io.github.subjekt.visitors

import io.github.subjekt.ExpressionBaseVisitor
import io.github.subjekt.ExpressionParser
import io.github.subjekt.nodes.expression.Node
import io.github.subjekt.utils.MessageCollector
import org.antlr.v4.runtime.ParserRuleContext

class ExpressionIrCreationVisitor(val messageCollector: MessageCollector) : ExpressionBaseVisitor<Node>() {

  private fun ParserRuleContext.createError(message: String) {
    messageCollector.error("Line ${this.start.line}: $message")
  }

  override fun visitCall(ctx: ExpressionParser.CallContext): Node? =
    visit(ctx.macroCall())

  override fun visitVariable(ctx: ExpressionParser.VariableContext): Node? =
    Node.Id(ctx.text, ctx.start.line)

  override fun visitPlusExpr(ctx: ExpressionParser.PlusExprContext): Node? {
    val left = visit(ctx.expression(0))
    val right = visit(ctx.expression(1))
    if (left == null) {
      ctx.createError("Left side of plus expression is null")
      return null
    }
    if (right == null) {
      ctx.createError("right side of plus expression is null")
      return null
    }
    return Node.Plus(
      visit(ctx.expression(0)),
      visit(ctx.expression(1)),
      ctx.start.line,
    )
  }

  override fun visitLiteral(ctx: ExpressionParser.LiteralContext): Node? =
    Node.Literal(ctx.text.trim().removePrefix("\"").removeSuffix("\""), ctx.start.line)

  override fun visitMacroCall(ctx: ExpressionParser.MacroCallContext): Node? {
    val id = ctx.ID()?.text
    if (id == null) {
      ctx.createError("macro call has no identifier")
      return null
    }
    val arguments = ctx.expression().map { visit(it) }
    return Node.Call(
      id,
      arguments,
      ctx.start.line,
    )
  }
}
