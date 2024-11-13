package io.github.subjekt.rendering

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.subjekt.files.Parameter
import io.github.subjekt.files.Subject
import io.github.subjekt.files.Suite
import io.github.subjekt.rendering.Permutations.permute

class Rendering(private val engine: Engine = EngineProvider.inject()) {

  private val logger = KotlinLogging.logger {}

  private fun mergeParameters(list1: List<Parameter>, list2: List<Parameter>): List<Parameter> =
    (list1 + list2)
      .groupBy { it.name }
      .map { (name, params) ->
        Parameter(
          name,
          params.flatMap { it.values }
        )
      }

  private fun Suite.getMacroInstances(): List<String> =
    (macros?.map { engine.renderMacroDeclaration(it) } ?: emptyList())
      .permute("\n")
      .run { ifEmpty { listOf("") } }

  private fun Suite.getParametersInstances(subject: Subject): List<Map<String, Any>> =
    (mergeParameters(parameters ?: emptyList(), subject.parameters ?: emptyList())).run {
      (this.permute()).run { ifEmpty { listOf(mapOf()) } }
    }

  private fun createWarning(subjectName: String, throwable: Throwable, code: String): String =
    """There was an error evaluating subject $subjectName:
        |   An exception was thrown: ${throwable.message}
        |Caused by the evaluation of:
        |   
        |$code
      """.trimMargin()

  fun Suite.render(): List<Set<String>> =
    subjects.map { subject ->
      val macroInstances = getMacroInstances()

      getParametersInstances(subject).flatMap { parameterInstance ->
        macroInstances.map { macroInstance ->
          val codeWithMacroDeclarations = if (macroInstance.isBlank()) subject.code else StringBuilder()
            .append(macroInstance)
            .append("\n")
            .append(subject.code)
            .toString()

          try {
            return@map engine.render(codeWithMacroDeclarations, parameterInstance)
          } catch (t: Throwable) {
            logger.error { createWarning(subject.name, t, codeWithMacroDeclarations) }
            return@map ""
          }
        }
      }.toSet()
    }


}
