package io.github.subjekt.compiler.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.utils.MessageCollector
import java.io.File

/**
 * Reader for YAML files.
 */
object Reader {

  private val mapper = ObjectMapper(YAMLFactory()).apply {
    findAndRegisterModules()

    SimpleModule().also {
      this.registerModule(ListHandlingModule())
    }
  }

  /**
   * Read a value of type [T] from a YAML [string].
   */
  internal inline fun <reified T : Any> readYaml(string: String): T = mapper.readValue<T>(string)

  /**
   * Parse a YAML [file] into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite] object
   * parsed from the YAML file, or null if an error occurred.
   */
  fun suiteFromYaml(file: File, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? = try {
    mapper.readValue(file)
  } catch (e: Exception) {
    messageCollector.error("Failed to parse YAML file: $file. Error: ${e.message}", Context.Companion.emptyContext(), -1)
    null
  }

  /**
   * Parse a YAML [yaml] string into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite]
   * object parsed from the YAML string, or null if an error occurred.
   */
  fun suiteFromYaml(yaml: String, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? = try {
    mapper.readValue(yaml)
  } catch (e: Exception) {
    messageCollector.error("Failed to parse YAML suite: \n$yaml\n\nError: ${e.message}", Context.Companion.emptyContext(), -1)
    null
  }

  /**
   * Parse a YAML resource [path] into a [Suite] object. It reports errors to the [messageCollector]. Returns the [Suite]
   * object parsed from the YAML resource, or null if an error occurred.
   */
  fun suiteFromResource(path: String, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? {
    object {}::class.java.classLoader.getResourceAsStream(path)?.bufferedReader().use {
      if (it == null) {
        messageCollector.error("Resource not found: $path", Context.Companion.emptyContext(), -1)
        return null
      }
      return suiteFromYaml(it.readText(), messageCollector)
    }
  }
}
