package io.github.subjekt

import io.github.subjekt.files.Subject
import io.github.subjekt.files.Suite
import io.github.subjekt.rendering.Rendering
import java.io.File
import java.nio.file.Path

class SubjektSuite(private val suite: Suite) {
  private val rendering: Rendering = Rendering()

  fun toKotlinSources(path: Path, nameGenerator: (Subject) -> String = { it.name }): List<File> =
    with(rendering) {
      val result = suite.render()
      require(result.size == suite.subjects.size)
      result.zip(suite.subjects).flatMap { (codeInstances, subject) ->
        codeInstances.withIndex().map { (index, code) ->
          val fileName = "${nameGenerator(subject)}$index.kt"
          val filePath = path.resolve(fileName)
          val file = filePath.toFile()
          file.writeText(code)

          file
        }
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
