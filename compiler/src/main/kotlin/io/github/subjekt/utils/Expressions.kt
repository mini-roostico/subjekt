package io.github.subjekt.utils

import io.github.subjekt.ExpressionLexer
import io.github.subjekt.ExpressionParser
import io.github.subjekt.nodes.Context
import io.github.subjekt.visitors.ExpressionIrCreationVisitor
import io.github.subjekt.visitors.ExpressionResolveVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

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
  fun String.evaluate(context: Context, messageCollector: MessageCollector): List<String> {
    val stream = CharStreams.fromString(this)
    val lexer = ExpressionLexer(stream)
    messageCollector.useLexer(lexer, context)
    val tokens = CommonTokenStream(lexer)
    val parser = ExpressionParser(tokens)
    messageCollector.useParser(parser, context)
    val tree = parser.expression()
    if (parser.numberOfSyntaxErrors > 0) {
      return emptyList()
    }
    val ast = ExpressionIrCreationVisitor(context, messageCollector).visit(tree)
    return ExpressionResolveVisitor(context, messageCollector).visit(ast)
  }
}
