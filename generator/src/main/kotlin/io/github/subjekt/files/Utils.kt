package io.github.subjekt.files

import io.github.subjekt.resolved.ResolvedSubject
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

object Utils {

  fun cleanName(name: String): String = name.replace(Regex("[^A-Za-z0-9 ]"), "")

  fun createUniqueFile(path: Path, nameGenerator: (ResolvedSubject) -> String, subject: ResolvedSubject): File {
    var fileName = cleanName(nameGenerator(subject)) + ".kt"
    var filePath = path.resolve(fileName)
    val redundancyIndex = 0
    while (filePath.exists()) {
      fileName = cleanName(nameGenerator(subject) + redundancyIndex) + ".kt"
      filePath = path.resolve(fileName)
    }
    return filePath.toFile()
  }

  fun createUniqueFile(path: Path, suiteName: String, otherDescription: String = ""): File {
    var fileName = cleanName(suiteName + otherDescription) + ".kt"
    var filePath = path.resolve(fileName)
    val redundancyIndex = 0
    while (filePath.exists()) {
      fileName = cleanName(suiteName + otherDescription + redundancyIndex) + ".kt"
      filePath = path.resolve(fileName)
    }
    return filePath.toFile()
  }
}
