package io.github.subjekt.nodes

import io.github.subjekt.conversion.CustomMacro
import io.github.subjekt.conversion.SubjektModule
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.resolved.DefinedCall
import io.github.subjekt.resolved.ResolvedParameter
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.yaml.Configuration
import java.lang.reflect.Modifier
import kotlin.reflect.full.findAnnotation

/**
 * Represents a context for the compiler. This context is used to store global parameters and macros for ONLY ONE
 * possible instance of a suite. Different parameter values correspond to different Contexts.
 */
data class Context(
  /**
   * The configuration of the wrapping suite.
   */
  var configuration: Configuration = Configuration(),
  /**
   * The map of global parameters. These parameters can be used in the bodies of the macros.
   */
  val parameters: MutableMap<String, Any> = mutableMapOf<String, Any>(),
  private val macros: MutableMap<String, Macro> = mutableMapOf<String, Macro>(),
  private val modules: MutableMap<String, Map<String, CustomMacro>> = mutableMapOf<String, Map<String, CustomMacro>>(),
  private val definedCalls: MutableMap<String, DefinedCall> = mutableMapOf<String, DefinedCall>(),
  /**
   * The name of the subject within which this context is currently being processed.
   */
  var subjektName: String = "",

  /**
   * The name of the suite within which this context is currently being processed.
   */
  var suiteName: String = "",
) {

  /**
   * Returns an immutable snapshot of the parameters.
   */
  fun parameterSnapshot(): Map<String, Any> = parameters.toMap()

  /**
   * Returns an immutable snapshot of the macros.
   */
  fun macroSnapshot(): Map<String, Macro> = macros.toMap()

  /**
   * Used to look up a parameter by its identifier. Returns null if the parameter is not defined.
   */
  fun lookupParameter(identifier: String): Any? = parameters[identifier]

  /**
   * Used to look up a macro by its identifier. Returns null if the macro is not defined.
   */
  fun lookupMacro(identifier: String): Macro? = macros[identifier]

  fun lookupDefinedMacro(identifier: String): DefinedCall? = definedCalls[identifier]

  /**
   * Used to look up a module's custom macro by its module and macro names. Returns null if the module or macro is not defined.
   */
  fun lookupModule(moduleName: String, macroName: String): CustomMacro? = modules[moduleName]?.get(macroName)

  /**
   * Puts a parameter in the context. If the parameter already exists, it will be overwritten.
   */
  fun putParameter(identifier: String, value: Any) {
    parameters[identifier] = value
  }

  /**
   * Puts a macro in the context. If the macro already exists, it will be overwritten.
   */
  fun putMacro(macro: Macro) {
    macros[macro.identifier] = macro
  }

  fun withParameters(parameters: Iterable<ResolvedParameter>): Context = copy(
    parameters = (parameters.associate { par -> par.identifier to par.value }
      .toMutableMap()).run { (this@Context.parameters + this).toMutableMap() },
  )

  fun withDefinedCalls(definedCalls: Iterable<DefinedCall>): Context = copy(
    definedCalls = (definedCalls.associate { call -> call.identifier to call }.toMutableMap()).run {
      (this@Context.definedCalls + this).toMutableMap()
    },
  )

  /**
   * Registers a module in the context. If the module already exists, it will be overwritten.
   *
   * A [module] must be an object annotated with [SubjektModule]. The module must contain static methods annotated with
   * [io.github.subjekt.conversion.Macro]. The module name used to call macros is defined by the [SubjektModule.name]
   * annotation parameter.
   *
   * Errors and warnings will be reported to the [messageCollector].
   */
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
    /**
     * Returns an empty context.
     */
    fun emptyContext(): Context = Context()

    /**
     * Returns a context with the given parameters.
     */
    fun of(
      vararg parameters: Pair<String, Any>,
    ) = Context().also { context ->
      parameters.forEach { context.putParameter(it.first, it.second) }
    }
  }
}
