package io.github.subjekt.visitors

import io.github.subjekt.ExpressionBaseVisitor
import io.github.subjekt.ExpressionParser

class ExpressionBaseVisitor : ExpressionBaseVisitor<Unit>() {
  override fun visitCall(ctx: ExpressionParser.CallContext?) {
    return super.visitCall(ctx)
  }

  override fun visitVariable(ctx: ExpressionParser.VariableContext?) {
    return super.visitVariable(ctx)
  }

  override fun visitPlusExpr(ctx: ExpressionParser.PlusExprContext?) {
    return super.visitPlusExpr(ctx)
  }

  override fun visitLiteral(ctx: ExpressionParser.LiteralContext?) {
    return super.visitLiteral(ctx)
  }

  override fun visitMacroCall(ctx: ExpressionParser.MacroCallContext?) {
    return super.visitMacroCall(ctx)
  }
}
