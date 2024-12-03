package io.github.subjekt.resolved

import io.github.subjekt.yaml.Configuration

sealed class ResolvedOutcome(open val message: String) {
  data class Warning(override val message: String) : ResolvedOutcome(message)
  data class Error(override val message: String) : ResolvedOutcome(message)
}

data class ResolvedSubject(
  val name: String,
  val code: String,
  val outcomes: List<ResolvedOutcome>,
)

data class ResolvedSuite(
  val name: String,
  val subjects: Set<ResolvedSubject>,
  val configuration: Configuration,
)
