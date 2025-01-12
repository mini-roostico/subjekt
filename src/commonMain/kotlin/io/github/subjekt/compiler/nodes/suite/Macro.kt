package io.github.subjekt.compiler.nodes.suite

import io.github.subjekt.compiler.resolved.Resolvable
import io.github.subjekt.compiler.yaml.Configuration

/**
 * Represents a macro definition node.
 */
class Macro(
    /**
   * The identifier of the macro.
   */
  val identifier: String,
    /**
   * The list of arguments identifiers.
   */
  val argumentsIdentifiers: List<String>,
    /**
   * The list of bodies of the macro. Each macro can have multiple, resolvable bodies.
   */
  val bodies: List<Resolvable>,
) {
  /**
   * The number of arguments the macro expects.
   */
  val argumentsNumber: Int
    get() = argumentsIdentifiers.size

  companion object {
    /**
     * Creates a Macro node from a YAML [macro] parsed data class. The [config] is used to parse the bodies.
     */
    fun fromYamlMacro(macro: io.github.subjekt.compiler.yaml.Macro, config: Configuration): Macro {
      if (!macro.def.contains("(")) {
        throw IllegalArgumentException("Illegal macro definition. Expected '(' in ${macro.def}")
      }
      val clean = macro.def.replace(" ", "")
      val identifier = clean.substringBefore("(")
      val arguments = clean.substringAfter("(").substringBefore(")").split(",").filter(String::isNotBlank)
      if (macro.values == null && macro.value == null) {
        throw IllegalArgumentException("Illegal macro definition. Expected 'values' or 'value' in $macro")
      }
      val bodies = macro.values?.map {
        Template.parse(it, config.expressionPrefix, config.expressionSuffix)
      } ?: listOf(Template.parse(macro.value!!, config.expressionPrefix, config.expressionSuffix))
      return Macro(identifier, arguments, bodies)
    }
  }
}
