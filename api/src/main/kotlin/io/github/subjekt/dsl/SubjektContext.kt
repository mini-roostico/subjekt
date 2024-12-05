package io.github.subjekt.dsl

import java.io.File

class SubjektContext {

  private val sources = mutableListOf<SubjektSource>()

  fun addSource(file: File) {
    sources.add(SubjektSource.fromFile(file))
  }

  fun addSource(pathToFile: String) {
    sources.add(SubjektSource.fromFile(File(pathToFile)))
  }

  fun addSourceDir(file: File) {
    file.walk().forEach {
      if (it.isFile && it.extension == "yaml") {
        sources.add(SubjektSource.fromFile(it))
      }
    }
  }

  fun getSources(): List<SubjektSource> {
    return sources.toList()
  }
}
