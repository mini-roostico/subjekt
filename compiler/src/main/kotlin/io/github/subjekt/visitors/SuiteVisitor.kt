package io.github.subjekt.visitors

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.Context.Companion.emptyContext
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.nodes.suite.Outcome
import io.github.subjekt.nodes.suite.Parameter
import io.github.subjekt.nodes.suite.Subject
import io.github.subjekt.nodes.suite.Suite
import io.github.subjekt.nodes.suite.Template
import io.github.subjekt.resolved.ResolvedSubject
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.utils.Permutations.permute
import io.github.subjekt.yaml.Configuration

class SuiteVisitor(private val messageCollector: MessageCollector) : SuiteIrVisitor<Unit> {

  private var context: Context = emptyContext()
  private lateinit var configuration: Configuration

  val resolvedSubjects = mutableSetOf<ResolvedSubject>()

  override fun visitSuite(suite: Suite) {
    configuration = suite.configuration
    suite.macros.forEach { mac -> visitMacro(mac) }
    val previousContext = context
    suite.parameters.permute { parConfiguration ->
      context = previousContext
      parConfiguration.forEach { par -> context.putParameter(par.identifier, par.value) }
      suite.subjects.forEach { sub -> visitSubject(sub) }
    }
    context = previousContext
  }

  override fun visitMacro(macro: Macro) {
    context.putMacro(macro)
  }

  override fun visitSubject(subject: Subject) {
    val previousContext = context
    subject.macros.map { it.toResolvedMacro(context, messageCollector) }.forEach { macro -> context.putMacro(macro) }
    subject.parameters.permute { parConfiguration ->
      context = previousContext
      parConfiguration.forEach { par -> context.putParameter(par.identifier, par.value) }
      val outcomes = subject.outcomes.map { it.toResolvedOutcome(context, messageCollector) }
      subject.code.resolve(context, messageCollector).forEach { code ->
        resolvedSubjects += ResolvedSubject(
          subject.name.resolveOne(context, messageCollector),
          code,
          outcomes,
        )
      }
    }
  }

  override fun visitOutcome(outcome: Outcome) {}

  override fun visitTemplate(template: Template) {}

  override fun visitParameter(parameter: Parameter) {}
}
