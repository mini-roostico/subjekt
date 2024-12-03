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

object SubjektCompiler {

  private fun io.github.subjekt.yaml.Suite?.resolve(messageCollector: MessageCollector): ResolvedSuite? {
    if (this == null) {
      return null
    }
    val suite = Suite.fromYamlSuite(this)
    val visitor = SuiteVisitor(messageCollector, listOf(Stdlib))
    visitor.visitSuite(suite)
    return ResolvedSuite(suite.name, visitor.resolvedSubjects.toSet(), suite.configuration)
  }

  fun compile(
    @Language("yaml") code: String,
    messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
  ): ResolvedSuite? = suiteFromYaml(code, messageCollector).resolve(messageCollector)

  fun compile(
    file: File,
    messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
  ): ResolvedSuite? = suiteFromYaml(file, messageCollector).resolve(messageCollector)

  fun compileResource(
    resource: String,
    messageCollector: MessageCollector = MessageCollector.SimpleCollector(),
  ): ResolvedSuite? = suiteFromResource(resource, messageCollector).resolve(messageCollector)
}
