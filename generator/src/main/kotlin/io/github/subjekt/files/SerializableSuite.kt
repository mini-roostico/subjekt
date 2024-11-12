package io.github.subjekt.files

typealias Parameter = MultiValued
typealias Macro = MultiValued

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

data class MultiValued(val name: String, val values: List<Any>)

data class Outcome(val warning: String)
