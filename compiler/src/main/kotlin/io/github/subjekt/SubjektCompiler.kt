package io.github.subjekt

import io.github.subjekt.conversion.Stdlib
import io.github.subjekt.nodes.suite.Suite
import io.github.subjekt.resolved.ResolvedSuite
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.visitors.SuiteVisitor
import io.github.subjekt.yaml.Reader.suiteFromResource
import io.github.subjekt.yaml.Reader.suiteFromYaml
import org.intellij.lang.annotations.Language
import java.io.File

/**
 * Entry point for the compiler of Subjekt YAML suites. It provides methods to compile YAML code, files, and resources
 * into [ResolvedSuite]s.
 */
object SubjektCompiler {

  /**
   * Resolves a nullable [Suite] into a [ResolvedSuite] using the Subjekt compiler. It returns null if the suite is null
   * or if an error occurred during the resolution.
   */
  private fun io.github.subjekt.yaml.Suite?.resolve(messageCollector: MessageCollector): ResolvedSuite? {
    if (this == null) {
      return null
    }
    val suite = Suite.fromYamlSuite(this)
    val visitor = SuiteVisitor(messageCollector, listOf(Stdlib))
    visitor.visitSuite(suite)
    return ResolvedSuite(
      suite.name,
      visitor.resolvedSubjects.filterNot { it.code.isBlank() || it.name.isBlank()}.toSet(),
      suite.configuration
    )
  }

  /**
   * Compiles a YAML [code] string into a [ResolvedSuite]. It returns null if an error occurred during the compilation.
   */
  fun compile(
    @Language("yaml") code: String,
    messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
  ): ResolvedSuite? = suiteFromYaml(code, messageCollector).resolve(messageCollector)

  /**
   * Compiles a YAML [file] into a [ResolvedSuite]. It returns null if an error occurred during the compilation.
   */
  fun compile(
    file: File,
    messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
  ): ResolvedSuite? = suiteFromYaml(file, messageCollector).resolve(messageCollector)

  /**
   * Compiles a YAML resource [resource] into a [ResolvedSuite]. It returns null if an error occurred during the compilation.
   */
  fun compileResource(
    resource: String,
    messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
  ): ResolvedSuite? = suiteFromResource(resource, messageCollector).resolve(messageCollector)
}
