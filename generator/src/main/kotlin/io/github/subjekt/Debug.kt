package io.github.subjekt

import io.github.subjekt.files.Reader
import io.github.subjekt.rendering.Rendering.render

object Debug {

  @JvmStatic
  fun main(vararg args: String) {
    Reader.suiteFromResource("subjects/ExampleSuite.yaml")?.render()
  }
}
