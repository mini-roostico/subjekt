package io.github.subjekt

import io.github.subjekt.dsl.SubjektContext
import io.github.subjekt.utils.MessageCollector

/**
 * The main entry point for the Subjekt DSL
 */
object Subjekt {
  /**
   * Reporter used to collect messages from the compiler.
   */
  val reporter: MessageCollector = MessageCollector.SimpleCollector()

  /**
   * Subjekt entry point where to specify the sources to be used..
   */
  fun subjekt(block: SubjektContext.() -> Unit): SubjektContext {
    return SubjektContext().apply(block)
  }
}
