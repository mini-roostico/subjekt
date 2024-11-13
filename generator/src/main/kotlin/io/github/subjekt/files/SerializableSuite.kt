package io.github.subjekt.files

data class Suite(
  val name: String,
  val subjects: List<Subject>,
  val macros: List<Macro>?,
  val parameters: List<Parameter>?
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
