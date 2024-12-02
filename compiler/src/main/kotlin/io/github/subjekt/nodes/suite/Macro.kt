package io.github.subjekt.nodes.suite

import io.github.subjekt.nodes.Context
import io.github.subjekt.resolved.Resolvable
import io.github.subjekt.utils.MessageCollector

class Macro(
  val identifier: String,
  val argumentsIdentifiers: List<String>,
  val bodies: List<Resolvable>,
) {

  val argumentsNumber: Int
    get() = argumentsIdentifiers.size

  fun toResolvedMacro(context: Context, messageCollector: MessageCollector): Macro {
    val selectedContext = Context.emptyContext().also {
      context.parameterSnapshot().filterNot { (name, _) -> argumentsIdentifiers.contains(name) }
        .forEach { (name, value) ->
          it.putParameter(name, value)
        }
    }
    return Macro(
      identifier,
      argumentsIdentifiers,
      bodies.map { Template.parse(it.resolveOne(selectedContext, messageCollector)) },
    )
  }

  companion object {
    fun fromYamlMacro(macro: io.github.subjekt.yaml.Macro): Macro {
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
