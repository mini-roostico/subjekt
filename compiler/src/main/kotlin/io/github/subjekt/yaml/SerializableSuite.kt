package io.github.subjekt.yaml

/**
 * Serializable representation of a suite. It can be converted to a [io.github.subjekt.nodes.suite.Suite] node.
 */
data class Suite(
  val name: String,
  val config: Configuration?,
  val macros: List<Macro>?,
  val subjects: List<Subject>,
  val parameters: List<Parameter>?,
)

/**
 * Serializable representation of a subject. It can be converted to a [io.github.subjekt.nodes.suite.Subject] node.
 */
data class Subject(
  val name: String,
  val parameters: List<Parameter>?,
  val macros: List<Macro>?,
  val code: String,
  val outcomes: List<Outcome>?,
  val properties: Map<String, Any>?,
)

/**
 * Serializable representation of a macro. It can be converted to a [io.github.subjekt.nodes.suite.Macro] node.
 */
data class Macro(
  /**
   * Textual definition of the macro in the form `macroName(param1, param2, ...)`.
   */
  val def: String,
  val values: List<String>?,
  val value: String?,
)

/**
 * Serializable representation of a parameter. It can be converted to a [io.github.subjekt.nodes.suite.Parameter] node.
 */
data class Parameter(
  val name: String,
  val values: List<String>?,
  val value: String?,
)

/**
 * Serializable representation of an outcome. It can be converted to a [io.github.subjekt.nodes.suite.Outcome] node.
 */
data class Outcome(val warning: String?, val error: String?)

/**
 * Serializable representation of the configuration of a suite. It also stores the default values for the configuration.
 * It can be used to store arbitrary key-value pairs.
 */
class Configuration : MutableMap<String, String> by mutableMapOf<String, String>() {
  val codePreamble: String
    get() = this["codePreamble"] ?: ""
  val expressionPrefix: String
    get() = this["expressionPrefix"] ?: "\${{"
  val expressionSuffix: String
    get() = this["expressionSuffix"] ?: "}}"
  val lint: Boolean
    get() = this["lint"]?.toBooleanStrictOrNull() != false // Default to true
}
