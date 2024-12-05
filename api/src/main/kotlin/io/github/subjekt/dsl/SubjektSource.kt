package io.github.subjekt.dsl

import io.github.subjekt.Subjekt
import io.github.subjekt.SubjektCompiler
import io.github.subjekt.resolved.ResolvedSubject
import io.github.subjekt.resolved.ResolvedSuite
import io.github.subjekt.yaml.Configuration
import java.io.File

class SubjektSource(val code: String) {

  private val suite: ResolvedSuite? by lazy {
    SubjektCompiler.compile(code, Subjekt.reporter)
  }

  val configuration: Configuration
    get() = suite?.configuration ?: Configuration()

  fun getGeneratedSubjects(): Set<ResolvedSubject> =
    if (suite == null) {
      emptySet()
    } else {
      suite?.subjects ?: emptySet()
    }

  companion object {
    fun fromFile(file: File): SubjektSource {
      return SubjektSource(file.readText())
    }
  }
}
