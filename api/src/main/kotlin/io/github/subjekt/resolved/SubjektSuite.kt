package io.github.subjekt.resolved

import io.github.subjekt.files.Outcome
import io.github.subjekt.files.Subject
import io.github.subjekt.files.Suite
import io.github.subjekt.files.Utils.createUniqueFile
import io.github.subjekt.rendering.Rendering
import io.github.subjekt.tests.KotestGenerator
import java.io.File
import java.nio.file.Path

class SubjektSuite(private val suite: Suite) {
  private val rendering: Rendering = Rendering()
  private val resolvedSubjects by lazy { with(rendering) { suite.resolve().flatten() } }

  val outcomes: Iterable<Iterable<Outcome>>
    get() = resolvedSubjects.map { it.outcomes }

  fun toKotlinSources(path: Path, nameGenerator: (ResolvedSubject) -> String = { it.name }): List<File> =
    resolvedSubjects.map { subject ->
      val file = createUniqueFile(path, nameGenerator, subject)
      file.writeText(SubjektConfiguration.codePreamble + "\n" + subject.code)
      file
    }

  fun generateTests(path: Path) {
    with(KotestGenerator(SubjektConfiguration.testPreamble)) {
      val tests = generateTests()
      val file = createUniqueFile(path, suite.name, "Spec")
      file.writeText(tests)
    }
  }

  fun blacklistByName(nameRegex: String): SubjektSuite =
    blacklist { it.name.matches(nameRegex.toRegex()) }

  fun whitelistByName(nameRegex: String): SubjektSuite =
    whitelist { it.name.matches(nameRegex.toRegex()) }

  fun blacklist(condition: (Subject) -> Boolean): SubjektSuite =
    SubjektSuite(suite.copy(subjects = suite.subjects.filterNot(condition)))

  fun whitelist(condition: (Subject) -> Boolean): SubjektSuite =
    SubjektSuite(suite.copy(subjects = suite.subjects.filter(condition)))

}
