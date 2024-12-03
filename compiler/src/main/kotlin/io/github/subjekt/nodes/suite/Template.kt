package io.github.subjekt.nodes.suite

import io.github.subjekt.nodes.Context
import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.utils.Expressions.evaluate
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.utils.Permutations.permute

data class Template(
  val toFormat: String,
  val expressions: List<String>,
  override val source: String,
) : Resolvable {

  override fun resolveOne(context: Context, messageCollector: MessageCollector): String {
    if (expressions.isEmpty()) return toFormat
    val firstResolvedExpression = (
      expressions.map { expr ->
        expr.evaluate(context, messageCollector).also {
          if (it.size > 1) {
            messageCollector.warning(
              "'resolveOne' was called inside template $source, but expression $expr has " +
                "multiple possible values. Taking only the first value.",
              context,
              -1,
            )
          }
        }.firstOrNull() ?: "".also {
          messageCollector.error(
            "Expression $expr in template $source could not be resolved.",
            context,
            -1,
          )
        }
      }
      ).toTypedArray()
    return toFormat.format(*firstResolvedExpression)
  }

  override fun resolve(context: Context, messageCollector: MessageCollector): Iterable<String> {
    if (expressions.isEmpty()) return listOf(toFormat)
    return expressions.map { expr -> expr.evaluate(context, messageCollector) }.permute()
      .map { toFormat.format(*it.toList().toTypedArray()) }
  }

  companion object {

    private fun processTemplate(input: String, prefix: String, suffix: String): Pair<String, List<String>> {
      val regex = Regex("""\Q$prefix\E(.*?)\Q$suffix\E""") // Match prefix ... suffix blocks
      val foundBlocks = mutableListOf<String>()

      val replaced = regex.replace(input) {
        foundBlocks.add(it.groupValues[1].trim())
        "%s"
      }

      return replaced to foundBlocks
    }

    fun parse(code: String, prefix: String = "\${{", suffix: String = "}}"): Template {
      val (template, blocks) = processTemplate(code, prefix, suffix)
      return Template(template, blocks, code)
    }
  }
}
