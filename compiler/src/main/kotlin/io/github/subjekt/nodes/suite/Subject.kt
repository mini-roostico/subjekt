package io.github.subjekt.nodes.suite

import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.yaml.Configuration

class Subject(
  val name: Resolvable,
  val macros: List<Macro> = emptyList(),
  val parameters: List<Parameter> = emptyList(),
  val code: Resolvable,
  val outcomes: List<Outcome> = emptyList(),
) {

  companion object {
    fun fromYamlSubject(yamlSubject: io.github.subjekt.yaml.Subject, config: Configuration): Subject {
      val name = Template.parse(yamlSubject.name, config.expressionPrefix, config.expressionSuffix)
      val macros = yamlSubject.macros?.map { Macro.fromYamlMacro(it, config) } ?: emptyList()
      val parameters = yamlSubject.parameters?.map { Parameter.fromYamlParameter(it) } ?: emptyList()
      val code = Template.parse(yamlSubject.code, config.expressionPrefix, config.expressionSuffix)
      return Subject(name, macros, parameters, code, yamlSubject.outcomes.map { Outcome.fromYamlOutcome(it, config) })
    }
  }
}
