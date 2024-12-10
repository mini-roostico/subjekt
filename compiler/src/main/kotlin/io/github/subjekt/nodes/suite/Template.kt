package io.github.subjekt.nodes.suite

import io.github.subjekt.nodes.Context
import io.github.subjekt.resolved.DefinedCall
import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.utils.Expressions.acceptExpressionVisitor
import io.github.subjekt.utils.Expressions.evaluate
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.visitors.ExpressionCallsVisitor

/**
 * Main [Resolvable] implementation. It represents a template with expressions that can be resolved into
 * multiple possible values.
 */
data class Template(
  val toFormat: String,
  val expressions: List<String>,
  override val source: String,
) : Resolvable {

  override fun resolve(context: Context, messageCollector: MessageCollector): String {
    if (expressions.isEmpty()) return toFormat
    return toFormat.format(
      *(expressions.map { expr -> expr.evaluate(context, messageCollector) }).toList().toTypedArray(),
    )
  }

  override fun resolveCalls(
    context: Context,
    messageCollector: MessageCollector,
  ): Iterable<DefinedCall> =
    if (expressions.isEmpty()) {
      emptySet()
    } else {
      expressions.flatMap {
        it.acceptExpressionVisitor(
          ExpressionCallsVisitor(context, messageCollector),
          context,
          messageCollector,
          emptySet<DefinedCall>(),
        )
      }
    }

  companion object {

    /**
     * Parses a template from a string [input] with a given [prefix] and [suffix]. It returns a [io.github.subjekt.nodes.suite.Template]
     * object with [toFormat] equal to a Kotlin format string and [expressions] equal to the list of expressions found in the template,
     * that can be used to format [toFormat].
     */
    private fun processTemplate(input: String, prefix: String, suffix: String): Pair<String, List<String>> {
      val regex = Regex("""\Q$prefix\E(.*?)\Q$suffix\E""") // Match prefix ... suffix blocks
      val foundBlocks = mutableListOf<String>()

      val replaced = regex.replace(input) {
        foundBlocks.add(it.groupValues[1].trim())
        "%s"
      }

      return replaced to foundBlocks
    }

    /**
     * Creates a [io.github.subjekt.nodes.suite.Template] object from a string [code] with a given [prefix] and [suffix]
     * used to delimit expressions in the code.
     */
    fun parse(code: String, prefix: String = "\${{", suffix: String = "}}"): Template {
      val (template, blocks) = processTemplate(code, prefix, suffix)
      return Template(template, blocks, code)
    }
  }
}
