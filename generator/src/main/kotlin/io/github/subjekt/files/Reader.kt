package io.github.subjekt.files

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

object Reader {

  private val mapper = ObjectMapper(YAMLFactory()).apply {
    findAndRegisterModules()
  }
  fun suiteFromYaml(path: File): Suite =
    mapper.readValue(path)

  fun suiteFromResource(path: String): Suite {
    val file = Reader::class.java.classLoader.getResource(path)?.path?.let { File(it) }
      ?: throw IllegalArgumentException("Resource not found: $path")
    return suiteFromYaml(file)
  }
}
