package io.github.subjekt.visitors

import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.nodes.suite.Outcome
import io.github.subjekt.nodes.suite.Parameter
import io.github.subjekt.nodes.suite.Subject
import io.github.subjekt.nodes.suite.Suite
import io.github.subjekt.nodes.suite.Template

interface SuiteIrVisitor<T> {
  fun visitSuite(suite: Suite): T
  fun visitSubject(subject: Subject): T
  fun visitParameter(parameter: Parameter): T
  fun visitOutcome(outcome: Outcome): T
  fun visitMacro(macro: Macro): T
  fun visitTemplate(template: Template): T
}
