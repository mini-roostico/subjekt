package io.github.subjekt.nodes

import io.github.subjekt.resolved.Resolvable

class Macro(
  val identifier: String,
  val argumentsIdentifiers: List<String>,
  val bodies: List<Resolvable>,
) {

  val argumentsNumber: Int
    get() = argumentsIdentifiers.size

  fun toResolvedMacro(context: Context): Macro {
    val selectedContext = Context().also {
      context.parameterSnapshot().filterNot { (name, _) -> argumentsIdentifiers.contains(name) }
        .forEach { (name, value) ->
          it.putParameter(name, value)
        }
    }
    return Macro(identifier, argumentsIdentifiers, bodies.map { Template.parse(it.resolveOne(selectedContext)) })
  }

  companion object {
    fun fromYamlMacro(macro: io.github.subjekt.yaml.Macro): io.github.subjekt.nodes.Macro {
      if (!macro.def.contains("(")) {
        throw IllegalArgumentException("Illegal macro definition. Expected '(' in ${macro.def}")
      }
      val clean = macro.def.replace(" ", "")
      val identifier = clean.substringBefore("(")
      val arguments = clean.substringAfter("(").substringBefore(")").split(",").filter(String::isNotBlank)
      val bodies = macro.values.map { Template.parse(it) }
      return Macro(identifier, arguments, bodies)
    }
  }
}
