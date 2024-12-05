package io.github.subjekt.resolved

import io.github.subjekt.nodes.Context
import io.github.subjekt.utils.MessageCollector

/**
 * Represents a resolvable value, therefore containing expressions that need to be resolved.
 */
interface Resolvable {

  /**
   * Resolves the resolvable value to a single string, even if the resolved expressions would lead to multiple possible
   * values.
   */
  fun resolveOne(context: Context, messageCollector: MessageCollector): String

  /**
   * Resolves the resolvable value to a list of strings, each representing a possible value of the resolved expressions.
   */
  fun resolve(context: Context, messageCollector: MessageCollector): Iterable<String>

  /**
   * The source of the resolvable value, used for debugging purposes.
   */
  val source: String
}
