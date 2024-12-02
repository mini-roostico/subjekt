package io.github.subjekt.nodes

import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.utils.Expressions.evaluate
import io.github.subjekt.utils.Permutations.permute

data class Template(
  val toFormat: String,
  val expressions: List<String>,
) : Resolvable {

  override fun resolveOne(context: Context): String {
    if (expressions.isEmpty()) return toFormat
    val firstResolvedExpression = (expressions.map { expr -> expr.evaluate(context).also {
      if (it.size > 1) context.warning("'resolveOne' was called inside template $toFormat, but expression $expr has" +
        "multiple possible values. Taking only the first value.")
    }.first() }).toTypedArray()
    return toFormat.format(*firstResolvedExpression)
  }

  override fun resolve(context: Context): Iterable<String> {
    if (expressions.isEmpty()) return listOf(toFormat)
    return expressions.map { expr -> expr.evaluate(context) }.permute()
      .map { toFormat.format(*it.toList().toTypedArray()) }
  }

  companion object {

    private fun processTemplate(input: String): Pair<String, List<String>> {
      val regex = Regex("""\$\{\{(.*?)}}""") // Match ${{ ... }} blocks
      val foundBlocks = mutableListOf<String>()

      val replaced = regex.replace(input) {
        foundBlocks.add(it.groupValues[1].trim())
        "%s"
      }

      return replaced to foundBlocks
    }

    fun parse(code: String): Template {
      val (template, blocks) = processTemplate(code)
      return Template(template, blocks)
    }
  }
}
