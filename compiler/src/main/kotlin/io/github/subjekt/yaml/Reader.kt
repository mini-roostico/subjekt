package io.github.subjekt.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.subjekt.nodes.Context
import io.github.subjekt.utils.MessageCollector
import java.io.File

object Reader {

  private val mapper = ObjectMapper(YAMLFactory()).apply {
    findAndRegisterModules()
  }

  fun suiteFromYaml(path: File, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? = try {
    mapper.readValue(path)
  } catch (e: Exception) {
    messageCollector.error("Failed to parse YAML file: $path. Error: ${e.message}", Context.emptyContext(), -1)
    null
  }

  fun suiteFromYaml(yaml: String, messageCollector: MessageCollector = MessageCollector.SimpleCollector()): Suite? = try {
    mapper.readValue(yaml)
  } catch (e: Exception) {
    messageCollector.error("Failed to parse YAML suite: \n$yaml\n\nError: ${e.message}", Context.emptyContext(), -1)
    null
  }

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
