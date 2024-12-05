package io.github.subjekt.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.subjekt.nodes.Context
import io.github.subjekt.utils.MessageCollector
import java.io.File

/**
 * Reader for YAML files.
 */
object Reader {

  private val mapper = ObjectMapper(YAMLFactory()).apply {
    findAndRegisterModules()
  }

  /**
   * Parse a YAML [file] into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite] object
   * parsed from the YAML file, or null if an error occurred.
   */
  fun suiteFromYaml(file: File, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? = try {
    mapper.readValue(file)
  } catch (e: Exception) {
    messageCollector.error("Failed to parse YAML file: $file. Error: ${e.message}", Context.emptyContext(), -1)
    null
  }

  /**
   * Parse a YAML [yaml] string into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite]
   * object parsed from the YAML string, or null if an error occurred.
   */
  fun suiteFromYaml(yaml: String, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? = try {
    mapper.readValue(yaml)
  } catch (e: Exception) {
    messageCollector.error("Failed to parse YAML suite: \n$yaml\n\nError: ${e.message}", Context.emptyContext(), -1)
    null
  }

  /**
   * Parse a YAML resource [path] into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite]
   * object parsed from the YAML resource, or null if an error occurred.
   */
  fun suiteFromResource(path: String, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? {
    object {}::class.java.classLoader.getResourceAsStream(path)?.bufferedReader().use {
      if (it == null) {
        messageCollector.error("Resource not found: $path", Context.emptyContext(), -1)
        return null
      }
      return suiteFromYaml(it.readText(), messageCollector)
    }
  }
}
