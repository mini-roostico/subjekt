package io.github.subjekt.nodes

import io.github.subjekt.conversion.CustomMacro
import io.github.subjekt.conversion.SubjektModule
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.yaml.Configuration
import java.lang.reflect.Modifier
import kotlin.reflect.full.findAnnotation

class Context(var configuration: Configuration = Configuration()) {

  val parameters = mutableMapOf<String, Any>()
  private val macros = mutableMapOf<String, Macro>()
  private val modules = mutableMapOf<String, Map<String, CustomMacro>>()
  var subjektName: String = ""
  var suiteName: String = ""

  fun parameterSnapshot(): Map<String, Any> = parameters.toMap()

  fun macroSnapshot(): Map<String, Macro> = macros.toMap()

  fun lookupParameter(identifier: String): Any? = parameters[identifier]

  fun lookupMacro(identifier: String): Macro? = macros[identifier]

  fun lookupModule(moduleName: String, macroName: String): CustomMacro? = modules[moduleName]?.get(macroName)

  fun putParameter(identifier: String, value: Any) {
    parameters[identifier] = value
  }

  fun putMacro(macro: Macro) {
    macros[macro.identifier] = macro
  }

  fun registerModule(module: Any, messageCollector: MessageCollector) {
    val clazz = module::class
    val annotation = clazz.findAnnotation<SubjektModule>()
    if (annotation == null) {
      messageCollector.error("Class ${clazz.simpleName} is not annotated with @SubjektModule", this, -1)
      return
    }
    val moduleName = annotation.name
    val staticMethods =
      clazz.java.declaredMethods.filter {
        it.isAnnotationPresent(io.github.subjekt.conversion.Macro::class.java)
      }.onEach {
        if (!Modifier.isStatic(it.modifiers)) {
          messageCollector.warning("Declared macro: '${it.name}' in module '$moduleName' is not static", this, -1)
        }
      }.filter { Modifier.isStatic(it.modifiers) }.filterNot { it.name.contains("$") }
    if (staticMethods.isEmpty()) {
      messageCollector.warning("Registered module '$moduleName' has no available methods", this, -1)
      return
    }
    staticMethods.mapNotNull { method ->
      CustomMacro.fromKotlinStatic(method, this, messageCollector)
    }.forEach { customMacro ->
      modules[moduleName] = modules.getOrDefault(moduleName, emptyMap()).plus(customMacro.id to customMacro)
    }
  }

  companion object {
    fun emptyContext(): Context = Context()

    fun of(
      vararg parameters: Pair<String, Any>,
    ) = Context().also { context ->
      parameters.forEach { context.putParameter(it.first, it.second) }
    }
  }
}
