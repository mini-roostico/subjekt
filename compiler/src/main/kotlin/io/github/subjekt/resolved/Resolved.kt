package io.github.subjekt.resolved

sealed class ResolvedOutcome(open val message: String) {
  data class Warning(override val message: String) : ResolvedOutcome(message)
  data class Error(override val message: String) : ResolvedOutcome(message)
}

data class ResolvedSubject(
  val name: String,
  val code: String,
  val outcomes: List<ResolvedOutcome>,
)
