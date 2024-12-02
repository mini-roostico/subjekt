package io.github.subjekt.visitors

import io.github.subjekt.ExpressionBaseVisitor
import io.github.subjekt.ExpressionLexer
import io.github.subjekt.ExpressionParser
import io.github.subjekt.nodes.Context
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class ExpressionBaseVisitor(context: Context) : ExpressionBaseVisitor<Unit>() {

  fun visit(expression: String): List<String> {
    val lexer = ExpressionLexer(CharStreams.fromString(expression))
    val parser = ExpressionParser(CommonTokenStream(lexer))
    val tree = parser.expression()
    visit(tree)
    TODO()
  }

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
