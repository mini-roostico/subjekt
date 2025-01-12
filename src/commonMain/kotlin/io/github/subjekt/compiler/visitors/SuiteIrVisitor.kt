package io.github.subjekt.compiler.visitors

import io.github.subjekt.compiler.nodes.suite.Macro
import io.github.subjekt.compiler.nodes.suite.Outcome
import io.github.subjekt.compiler.nodes.suite.Parameter
import io.github.subjekt.compiler.nodes.suite.Subject
import io.github.subjekt.compiler.nodes.suite.Suite
import io.github.subjekt.compiler.nodes.suite.Template

/**
 * Visitor for the intermediate representation of a suite. Unlike the [io.github.subjekt.compiler.nodes.expression.Node]
 * representation, this visitor visits the nodes specified in the [io.github.subjekt.nodes.suite] package.
 */
interface SuiteIrVisitor<T> {
  /**
   * Visits a [Suite].
   */
  fun visitSuite(suite: Suite): T

  /**
   * Visits a [Subject].
   */
  fun visitSubject(subject: Subject): T

  /**
   * Visits a [Parameter].
   */
  fun visitParameter(parameter: Parameter): T

  /**
   * Visits an [Outcome].
   */
  fun visitOutcome(outcome: Outcome): T

  /**
   * Visits a [Macro].
   */
  fun visitMacro(macro: Macro): T

  /**
   * Visits a [Template].
   */
  fun visitTemplate(template: Template): T
}
