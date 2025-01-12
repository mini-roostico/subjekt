package io.github.subjekt.generators

import io.github.subjekt.Subjekt
import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.linting.Linter
import io.github.subjekt.resolved.ResolvedSubject

/**
 * Generator for subjects from a SubjektContext
 */
object SubjectGenerator {

  /**
   * Resolves the subjects from a SubjektContext and returns them as a set of ResolvedSubjects, applying linting if
   * necessary and adding the preamble to the code
   */
  fun SubjektContext.toResolvedSubjects(): Set<ResolvedSubject> {
    return getSources().flatMap { source ->
      source.getGeneratedSubjects().map {
        val preamble = source.configuration.codePreamble
        var code = (if (preamble.isNotBlank()) preamble + "\n" else "") + it.code
        if (source.configuration.lint) {
          code = Linter.lint(code, Subjekt.reporter)
        }
        ResolvedSubject(it.name, code, it.outcomes)
      }
    }.toSet()
  }
}
