package io.github.subjekt.nodes.suite

import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.yaml.Configuration

class Macro(
  val identifier: String,
  val argumentsIdentifiers: List<String>,
  val bodies: List<Resolvable>,
) {

  val argumentsNumber: Int
    get() = argumentsIdentifiers.size

  companion object {
    fun fromYamlMacro(macro: io.github.subjekt.yaml.Macro, config: Configuration): Macro {
      if (!macro.def.contains("(")) {
        throw IllegalArgumentException("Illegal macro definition. Expected '(' in ${macro.def}")
      }
      val clean = macro.def.replace(" ", "")
      val identifier = clean.substringBefore("(")
      val arguments = clean.substringAfter("(").substringBefore(")").split(",").filter(String::isNotBlank)
      val bodies = macro.values.map { Template.parse(it, config.expressionPrefix, config.expressionSuffix) }
      return Macro(identifier, arguments, bodies)
    }
  }
}
