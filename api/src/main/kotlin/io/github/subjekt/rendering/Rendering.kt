package io.github.subjekt.rendering

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.subjekt.files.*
import io.github.subjekt.rendering.Permutations.permute
import io.github.subjekt.resolved.ResolvedSubject
import io.github.subjekt.resolved.SubjektConfiguration

class Rendering(private val engine: Engine = EngineProvider.inject(SubjektConfiguration.engine)) {

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

  private fun List<Macro>?.getMacroInstances(): List<String> =
    (this?.map { engine.renderMacroDeclaration(it) } ?: emptyList())
      .permute("\n")
      .run { ifEmpty { listOf("") } }

  private fun Subject.getParametersInstances(suiteParameters: List<Parameter>?): List<Map<String, Any>> =
    (mergeParameters(suiteParameters ?: emptyList(), parameters ?: emptyList())).run {
      (this.permute()).run { ifEmpty { listOf(mapOf()) } }
    }

  private fun createWarning(subjectName: String, throwable: Throwable, code: String): String =
    """There was an error evaluating subject $subjectName:
        |   An exception was thrown: ${throwable.message}
        |Caused by the evaluation of:
        |   
        |$code
      """.trimMargin()

  private fun Subject.resolve(suiteMacros: List<Macro>?, suiteParameters: List<Parameter>?): Set<ResolvedSubject> {
    val macroInstances = suiteMacros.getMacroInstances()
    return getParametersInstances(suiteParameters).flatMap { parameterInstance ->
      macroInstances.mapNotNull { macroInstance ->
        val macroDeclarations = if (macroInstance.isBlank()) code else StringBuilder()
          .append(macroInstance)
          .append("\n")
          .toString()

        try {
          val resolvedName = engine.render(macroDeclarations + name, parameterInstance).replace("\n", "")
          val resolvedOutcomes = outcomes
            .filterNot { it.error == null && it.warning == null }
            .map {
              Outcome(
                warning = it.warning?.run { engine.render(macroDeclarations + this, parameterInstance).replace("\n", "") },
                error = it.error?.run { engine.render(macroDeclarations + this, parameterInstance).replace("\n", "") }
              )
            }
          val resolvedCode = engine.render(macroDeclarations + code, parameterInstance)
          ResolvedSubject(resolvedName, resolvedCode, resolvedOutcomes)
        } catch (t: Throwable) {
          logger.error { createWarning(name, t, macroDeclarations + code) }
          null
        }
      }
    }.toSet()
  }

  private fun makeConfiguration(configuration: Configuration) {
    SubjektConfiguration.engine = configuration.engine
    SubjektConfiguration.lint = configuration.lint
    SubjektConfiguration.testFormat = configuration.testFormat
    SubjektConfiguration.codePreamble = configuration.codePreamble
    SubjektConfiguration.testPreamble = configuration.testPreamble
    SubjektConfiguration.hardCodedTests = configuration.hardCodedTests
  }

  fun Suite.resolve(): List<Set<ResolvedSubject>> {
    this.config?.let { makeConfiguration(it) }
    return subjects.map { it.resolve(macros, parameters) }
  }


}
