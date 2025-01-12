package io.github.subjekt.linting

import com.pinterest.ktlint.rule.engine.api.Code
import com.pinterest.ktlint.rule.engine.api.KtLintRuleEngine
import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import io.github.subjekt.nodes.Context
import io.github.subjekt.utils.MessageCollector

/**
 * Linter object that uses KtLint to lint and format code.
 */
object Linter {
  private val runtimeLoadedRuleProviders: Set<RuleProvider> = setOf(
    *StandardRuleSetProvider().getRuleProviders().toTypedArray(),
  )

  private val apiConsumerKtLintRuleEngine = KtLintRuleEngine(ruleProviders = runtimeLoadedRuleProviders)

  /**
   * Lints the given [code] and returns the linted code.
   */
  fun lint(code: String, messageCollector: MessageCollector): String {
    val codeFile = Code.fromSnippet(code)
    val violations = mutableListOf<String>()

    apiConsumerKtLintRuleEngine
      .lint(codeFile) {
        violations.add(it.toString())
      }
    violations.forEach { message ->
      messageCollector.info(message, Context.emptyContext(), -1)
    }
    val linted = apiConsumerKtLintRuleEngine.format(codeFile) { _ -> AutocorrectDecision.ALLOW_AUTOCORRECT }
    return linted
  }
}
