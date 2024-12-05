package io.github.subjekt

import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.utils.MessageCollector

object Subjekt {

  val reporter: MessageCollector = MessageCollector.SimpleCollector()

  fun subjekt(block: SubjektContext.() -> Unit): SubjektContext {
    return SubjektContext().apply(block)
  }
}
