package io.github.subjekt.rendering

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.subjekt.files.Macro
import io.github.subjekt.files.Parameter
import io.github.subjekt.files.Suite
import io.github.subjekt.rendering.Permutations.permute
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringWriter

object Rendering {

  private val logger = KotlinLogging.logger {}

  fun mergeParameters(list1: List<Parameter>, list2: List<Parameter>): List<Parameter> {
    return (list1 + list2)
      .groupBy { it.name }
      .map { (name, params) ->
        Parameter(
          name,
          params.flatMap { it.values } // Merge and remove duplicates if needed
        )
      }
  }

  fun Macro.renderVelocityMacroDeclarations(): List<String> = this.values.map {
    """
      |#macro(${this.name} ${this.accepts.joinToString(" $", prefix = "$")})$it#end
    """.trimMargin()
  }

  fun Suite.render(): Set<Set<String>> {
    return this.subjects.map { subject ->
      val macroInstances = (macros?.map { it.renderVelocityMacroDeclarations() } ?: emptyList())
        .permute("\n")
        .run { ifEmpty { listOf("") } }
      val parameterMerged = mergeParameters(parameters ?: emptyList(), subject.parameters ?: emptyList())
      val parameterInstances = (parameterMerged.permute()).run { ifEmpty { listOf(mapOf()) } }
      parameterInstances.flatMap { parameterMap ->
        macroInstances.map { macroInstance ->
          val codeWithMacros = if (macroInstance.isBlank()) subject.code else StringBuilder()
            .append(macroInstance)
            .append("\n")
            .append(subject.code)
            .toString()

          val velocity = VelocityEngine()
          velocity.init()
          val context = VelocityContext()
          parameterMap.forEach { (key, value) -> context.put(key, value) }

          val writer = StringWriter()
          try {
            velocity.evaluate(context, writer, "StringTemplate", codeWithMacros)
            return@map writer.toString()
          } catch (t: Throwable) {
            logger.error {
              """There was an error evaluating subject ${subject.name}:
                |   An exception was thrown: ${t.message}
                |Caused by the evaluation of:
                |   
                |$codeWithMacros
              """.trimMargin()
            }
            return@map ""
          }
        }
      }.toSet()
    }.toSet()
  }

}
