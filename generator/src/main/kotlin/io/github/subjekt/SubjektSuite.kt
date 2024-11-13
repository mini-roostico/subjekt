package io.github.subjekt

import io.github.subjekt.files.Subject
import io.github.subjekt.files.Suite
import io.github.subjekt.rendering.Rendering
import io.github.subjekt.resolved.ResolvedSubject
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

class SubjektSuite(private val suite: Suite) {
  private val rendering: Rendering = Rendering()

  private fun createUniqueFile(path: Path, nameGenerator: (ResolvedSubject) -> String, subject: ResolvedSubject): File {
    fun cleanName(name: String): String = name.replace(Regex("[^A-Za-z0-9 ]"), "")


    var fileName = cleanName(nameGenerator(subject)) + ".kt"
    var filePath = path.resolve(fileName)
    val redundancyIndex = 0
    while (filePath.exists()) {
      fileName = cleanName(nameGenerator(subject) + redundancyIndex) + ".kt"
      filePath = path.resolve(fileName)
    }
    return filePath.toFile()
  }

  fun toKotlinSources(path: Path, nameGenerator: (ResolvedSubject) -> String = { it.name }): List<File> =
    with(rendering) {
      val result = suite.resolve()
      result.flatMap { resolvedSubjects ->
        resolvedSubjects.map { subject ->
          val file = createUniqueFile(path, nameGenerator, subject)
          file.writeText(subject.code)
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
