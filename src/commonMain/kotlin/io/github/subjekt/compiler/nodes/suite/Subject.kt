package io.github.subjekt.compiler.nodes.suite

import io.github.subjekt.compiler.resolved.Resolvable
import io.github.subjekt.compiler.yaml.Configuration

/**
 * Represents a subject node.
 */
class Subject(
    /**
   * The name of the subject. This is a resolvable value, meaning it can be a template with expressions.
   */
  val name: Resolvable,
    /**
   * The list of internal macros used in the subject. These macros can use global parameters in their bodies.
   */
  val macros: List<Macro> = emptyList(),
    /**
   * The list of parameters used in the subject.
   */
  val parameters: List<Parameter> = emptyList(),
    /**
   * The code of the subject. This is a resolvable value, meaning it can be a template with expressions.
   */
  val code: Resolvable,
    /**
   * The list of outcomes of the subject.
   */
  val outcomes: List<Outcome> = emptyList(),
    /**
   * Additional properties that can be used to store arbitrary key-value pairs.
   */
  val properties: Map<String, Resolvable> = emptyMap(),
) {

  companion object {
    /**
     * Creates a Subject node from a YAML [yamlSubject] parsed data class. The [config] is used to parse the name and code.
     */
    fun fromYamlSubject(yamlSubject: io.github.subjekt.compiler.yaml.Subject, config: Configuration): Subject {
      val name = Template.parse(yamlSubject.name, config.expressionPrefix, config.expressionSuffix)
      val macros = yamlSubject.macros?.map { Macro.fromYamlMacro(it, config) } ?: emptyList()
      val parameters = yamlSubject.parameters?.map { Parameter.fromYamlParameter(it) } ?: emptyList()
      val code = Template.parse(yamlSubject.code, config.expressionPrefix, config.expressionSuffix)
      return Subject(
        name,
        macros,
        parameters,
        code,
        yamlSubject.outcomes?.map { Outcome.fromYamlOutcome(it, config) } ?: emptyList(),
        yamlSubject.properties?.mapValues {
            (_, value) ->
          Template.parse(value, config.expressionPrefix, config.expressionSuffix)
        } ?: emptyMap(),
      )
    }
  }
}
