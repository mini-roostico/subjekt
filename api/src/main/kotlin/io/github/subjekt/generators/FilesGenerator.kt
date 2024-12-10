package io.github.subjekt.generators

import io.github.subjekt.Subjekt
import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.files.Utils.cleanName
import io.github.subjekt.linting.Linter
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
    getSources().forEach { source ->
      val generatedSubjects = source.getGeneratedSubjects()
      generatedSubjects.forEach {
        var fileName = cleanName(it.name)
        var file = File("$path/$fileName.$extension")
        if (file.exists()) {
          file.delete()
        }
        val preamble = source.configuration.codePreamble
        var code = preamble + "\n" + it.code
        if (source.configuration.lint) {
          code = Linter.lint(code, Subjekt.reporter)
        }
        file.writeText(code)
      }
    }
  }
}
