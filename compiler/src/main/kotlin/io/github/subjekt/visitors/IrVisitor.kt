package io.github.subjekt.visitors

import io.github.subjekt.nodes.Macro
import io.github.subjekt.nodes.Outcome
import io.github.subjekt.nodes.Parameter
import io.github.subjekt.nodes.Subject
import io.github.subjekt.nodes.Suite
import io.github.subjekt.nodes.Template

interface IrVisitor<T> {
  fun visitSuite(suite: Suite): T
  fun visitSubject(subject: Subject): T
  fun visitParameter(parameter: Parameter): T
  fun visitOutcome(outcome: Outcome): T
  fun visitMacro(macro: Macro): T
  fun visitTemplate(template: Template): T
}
