package io.github.subjekt.visitors

import io.github.subjekt.ExpressionBaseVisitor
import io.github.subjekt.ExpressionParser
import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.expression.Node

class ExpressionIrCreationVisitor(context: Context) : ExpressionBaseVisitor<Node>() {

  override fun visitCall(ctx: ExpressionParser.CallContext?): Node? {
    return super.visitCall(ctx)
  }

  override fun visitVariable(ctx: ExpressionParser.VariableContext?): Node? {
    return super.visitVariable(ctx)
  }

  override fun visitPlusExpr(ctx: ExpressionParser.PlusExprContext?): Node? {
    return super.visitPlusExpr(ctx)
  }

  override fun visitLiteral(ctx: ExpressionParser.LiteralContext?): Node? {
    return super.visitLiteral(ctx)
  }

  override fun visitMacroCall(ctx: ExpressionParser.MacroCallContext?): Node? {
    return super.visitMacroCall(ctx)
  }
}
