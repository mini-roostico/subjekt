package io.github.subjekt.compiler.nodes.suite

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.resolved.Resolvable
import io.github.subjekt.compiler.resolved.ResolvedOutcome
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.compiler.yaml.Configuration

/**
 * Represents an outcome definition node.
 */
sealed class Outcome(
    /**
   * The message to be displayed when the outcome is triggered.
   */
  open val message: Resolvable,
) {
  /**
   * Represents a warning outcome.
   */
  data class Warning(override val message: Resolvable) : Outcome(message)

  /**
   * Represents an error outcome.
   */
  data class Error(override val message: Resolvable) : Outcome(message)

  /**
   * Resolves the outcome message with the given [context] and reporting to the [messageCollector].
   */
  fun toResolvedOutcome(context: Context, messageCollector: MessageCollector): ResolvedOutcome = when (this) {
    is Warning -> ResolvedOutcome.Warning(message.resolve(context, messageCollector))
    is Error -> ResolvedOutcome.Error(message.resolve(context, messageCollector))
  }

  companion object {
    /**
     * Creates an Outcome node from a YAML [outcome] parsed data class. The [config] is used to parse the message.
     */
    fun fromYamlOutcome(outcome: io.github.subjekt.compiler.yaml.Outcome, config: Configuration): Outcome = if (outcome.warning != null) {
      Warning(Template.parse(outcome.warning, config.expressionPrefix, config.expressionSuffix))
    } else if (outcome.error != null) {
      Error(Template.parse(outcome.error, config.expressionPrefix, config.expressionSuffix))
    } else {
      throw IllegalArgumentException("Illegal outcome definition. Expected 'warning' or 'error' in $outcome")
    }
  }
}