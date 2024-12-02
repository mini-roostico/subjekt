package io.github.subjekt.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

object Reader {

  private val mapper = ObjectMapper(YAMLFactory()).apply {
    findAndRegisterModules()
  }

  fun suiteFromYaml(path: File): Suite? =
    try { mapper.readValue(path) } catch (e: Exception) { println("There was an error: ${e.message}"); null }

  fun suiteFromYaml(yaml: String): Suite? =
    try { mapper.readValue(yaml) } catch (e: Exception) { println("There was an error; ${e.message}"); null }

  fun suiteFromResource(path: String): Suite? {
    val file = Reader::class.java.classLoader.getResource(path)?.path?.let { File(it) } ?: return null
    return suiteFromYaml(file)
  }

}
