package io.github.subjekt.yaml

data class Suite(
  val name: String,
  val config: Configuration?,
  val macros: List<Macro>?,
  val subjects: List<Subject>,
  val parameters: List<Parameter>?,
)

data class Subject(
  val name: String,
  val parameters: List<Parameter>?,
  val macros: List<Macro>?,
  val code: String,
  val outcomes: List<Outcome>,
)

data class Macro(val def: String, val values: List<String>)

data class Parameter(val name: String, val values: List<Any>)

data class Outcome(val warning: String?, val error: String?)

data class Configuration(
  val codePreamble: String = "",
  val expressionPrefix: String = "\${{",
  val expressionSuffix: String = "}}",
)
