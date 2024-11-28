package io.github.subjekt.resolved

import io.github.subjekt.files.Outcome

data class ResolvedSubject(val name: String, val code: String, val outcomes: List<Outcome>)
