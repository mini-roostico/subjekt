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

/**
 * Main visitor used by the compiler to resolve a [Suite] to a list of [ResolvedSubject]s (and therefore a
 * [io.github.subjekt.resolved.ResolvedSuite]). It must be called to visit a [Suite] node converted from the YAML
 * input one.
 */
class SuiteVisitor(
  /**
   * Message collector to report errors.
   */
  private val messageCollector: MessageCollector,
  /**
   * List of modules to register in the context. By default, all the macros not found in the current context will be
   * searched inside the `std` module, which is automatically added to this list.
   */
  modules: List<Any> = emptyList(),
) : SuiteIrVisitor<Unit> {

  private var context: Context = emptyContext()

  /**
   * List of resolved subjects found in the suite, result of this visitor.
   */
  val resolvedSubjects = mutableSetOf<ResolvedSubject>()

  init {
    modules.forEach { module -> context.registerModule(module, messageCollector) }
  }

  override fun visitSuite(suite: Suite) {
    context.suiteName = suite.name
    context.configuration = suite.configuration
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
    context.subjektName = subject.name.source
    val previousContext = context
    subject.macros.forEach { macro -> context.putMacro(macro) }
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
