package io.github.subjekt.nodes

import io.github.subjekt.nodes.suite.Macro

class Context() {

  val parameters = mutableMapOf<String, Any>()
  private val macros = mutableMapOf<String, Macro>()
  var subjektName: String = ""
  var suiteName: String = ""

  fun parameterSnapshot(): Map<String, Any> =
    parameters.toMap()

  fun macroSnapshot(): Map<String, Macro> =
    macros.toMap()

  fun lookupParameter(identifier: String): Any? {
    return parameters[identifier]
  }

  fun lookupMacro(identifier: String): Macro? {
    return macros[identifier]
  }

  fun putParameter(identifier: String, value: Any) {
    parameters[identifier] = value
  }

  fun putMacro(macro: Macro) {
    macros[macro.identifier] = macro
  }

  companion object {
    fun emptyContext(): Context {
      return Context()
    }

    fun of(
      vararg parameters: Pair<String, Any>,
    ) = Context().also { context ->
      parameters.forEach { context.putParameter(it.first, it.second) }
    }
  }
}
