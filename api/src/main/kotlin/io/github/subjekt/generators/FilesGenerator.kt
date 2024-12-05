package io.github.subjekt.generators

import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.files.Utils.cleanName
import java.io.File

object FilesGenerator {

  fun SubjektContext.toFiles(path: String, extension: String) {
    val dir = File(path)
    dir.mkdirs()
    getSources().forEach { source ->
      val generatedSubjects = source.getGeneratedSubjects()
      generatedSubjects.forEach {
        var fileName = cleanName(it.name)
        var file = File("$path/$fileName.$extension")
        var redundancyIndex = 1
        while (file.exists()) {
          var fileName = cleanName(it.name) + redundancyIndex
          file = File("$path/$fileName.$extension")
          redundancyIndex++
        }
        val preamble = source.configuration.codePreamble
        file.writeText(preamble + "\n" + it.code)
      }
    }
  }
}
