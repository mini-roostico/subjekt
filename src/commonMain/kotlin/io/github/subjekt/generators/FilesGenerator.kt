package io.github.subjekt.generators

import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.files.Utils.cleanName
import io.github.subjekt.generators.SubjectGenerator.toResolvedSubjects
import java.io.File

/**
 * Generator for files from a SubjektContext
 */
object FilesGenerator {

  /**
   * Generates files from a SubjektContext at the specified [path] with the specified [extension]
   */
  fun SubjektContext.toFiles(path: String, extension: String) {
    val dir = File(path)
    dir.mkdirs()
    this.toResolvedSubjects().forEach { subject ->
      var fileName = cleanName(subject.name)
      var file = File("$path/$fileName.$extension")
      if (file.exists()) {
        file.delete()
      }
      file.writeText(subject.code)
    }
  }

  /**
   * Generates temporary files from a SubjektContext, associating each file with its cleaned name
   */
  fun SubjektContext.toTempFiles(): Map<String, File> =
    this.toResolvedSubjects().associate { subject ->
      var fileName = cleanName(subject.name)
      var file = File.createTempFile(fileName, ".kt")
      file.writeText(subject.code)
      fileName to file
    }
}
