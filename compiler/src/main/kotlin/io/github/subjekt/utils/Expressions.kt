package io.github.subjekt.utils

import io.github.subjekt.ExpressionLexer
import io.github.subjekt.ExpressionParser
import io.github.subjekt.nodes.Context
import io.github.subjekt.visitors.ExpressionIrCreationVisitor
import io.github.subjekt.visitors.ExpressionResolveVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

object Expressions {

  fun String.evaluate(context: Context, messageCollector: MessageCollector): List<String> {
    val stream = CharStreams.fromString(this)
    val lexer = ExpressionLexer(stream)
    messageCollector.useLexer(lexer)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    messageCollector.useParser(parser)
    val tree = parser.expression()
    if (parser.numberOfSyntaxErrors > 0) {
      return emptyList()
    }
    val ast = ExpressionIrCreationVisitor(messageCollector).visit(tree)
    return ExpressionResolveVisitor(context, messageCollector).visit(ast)
  }

}
