package io.github.subjekt.compiler.resolved

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.utils.MessageCollector

/**
 * Represents a resolvable value, therefore containing expressions that need to be resolved.
 */
interface Resolvable {
  /**
   * Resolves the resolvable value to a list of strings, each representing a possible value of the resolved expressions.
   */
  fun resolve(context: Context, messageCollector: MessageCollector): String

  /**
   * Resolves all the calls present in this resolvable, returned as an Iterable of [DefinedCall]s.
   */
  fun resolveCalls(context: Context, messageCollector: MessageCollector): Iterable<DefinedCall>

  /**
   * The source of the resolvable value, used for debugging purposes.
   */
  val source: String
}