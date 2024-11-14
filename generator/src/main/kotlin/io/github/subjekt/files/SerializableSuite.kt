package io.github.subjekt.files

data class Suite(
  val name: String,
  val subjects: List<Subject>,
  val macros: List<Macro>?,
  val parameters: List<Parameter>?,
  val config: Configuration? = null
)

data class Subject(
  val name: String,
  val parameters: List<Parameter>?,
  val code: String,
  val outcomes: List<Outcome>
)

data class Macro(val name: String, val values: List<Any>, val accepts: List<String>)

data class Parameter(val name: String, val values: List<Any>)

data class Outcome(val warning: String?, val error: String?)

data class Configuration(
  val engine: String = "velocity",
  val lint: Boolean = false,
  val testFormat: String = "kotest",
  val mergeTests: Boolean = true
) {

}
