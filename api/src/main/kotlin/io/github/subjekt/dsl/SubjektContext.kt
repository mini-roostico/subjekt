package io.github.subjekt.dsl

import java.io.File

/**
 * Main context used in the DSL.
 */
class SubjektContext {

  private val sources = mutableListOf<SubjektSource>()

  /**
   * Adds a source YAML [file] to be compiled.
   */
  fun addSource(file: File) {
    sources.add(SubjektSource.fromFile(file))
  }

  /**
   * Adds a source YAML file at path [pathToFile] to be compiled.
   */
  fun addSource(pathToFile: String) {
    sources.add(SubjektSource.fromFile(File(pathToFile)))
  }

  /**
   * Adds all YAML files in the directory [dir] to be compiled.
   */
  fun addSourceDir(dir: File) {
    dir.walk().forEach {
      if (it.isFile && it.extension == "yaml") {
        sources.add(SubjektSource.fromFile(it))
      }
    }
  }

  /**
   * Returns a list of all sources added to the context.
   */
  fun getSources(): List<SubjektSource> {
    return sources.toList()
  }
}
