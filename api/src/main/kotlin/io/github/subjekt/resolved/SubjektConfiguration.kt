package io.github.subjekt.resolved

object SubjektConfiguration {

  var engine: String = "velocity"
  var lint: Boolean = false
  var codePreamble: String = ""

  var testFormat: String = "kotest"
  var testPreamble: String = ""
  var hardCodedTests: Boolean = false
}
