package io.github.subjekt.nodes

import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.utils.MessageCollector

class Context(private val collector: MessageCollector) {

  val parameter = mutableMapOf<String, Any>()
  private val macros = mutableMapOf<String, Macro>()

  fun parameterSnapshot(): Map<String, Any> =
    parameter.toMap()

  fun macroSnapshot(): Map<String, Macro> =
    macros.toMap()

  operator fun plus(pairs: Context): Context {
    TODO()
  }

  fun putParameter(identifier: String, value: Any) {
    parameter[identifier] = value
  }

  fun putMacro(macro: Macro) {
    macros[macro.identifier] = macro
  }

  fun warning(message: String) {
    TODO()
  }

  fun error(message: String) {
    TODO()
  }


  companion object {
    fun emptyContext(): Context {
      return Context(MessageCollector.NullCollector())
    }

    fun of(
      vararg parameters: Pair<String, Any>,
      messageCollector: MessageCollector = MessageCollector.NullCollector()
    ) = Context(messageCollector).also { context ->
      parameters.forEach { context.putParameter(it.first, it.second) }
    }
  }
}
