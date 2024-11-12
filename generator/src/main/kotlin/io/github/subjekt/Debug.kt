package io.github.subjekt

import io.github.subjekt.files.Reader

object Debug {

  @JvmStatic
  fun main(vararg args: String) {
    println(Reader.suiteFromResource("subjects/ExampleSuite.yaml"))
  }
}
