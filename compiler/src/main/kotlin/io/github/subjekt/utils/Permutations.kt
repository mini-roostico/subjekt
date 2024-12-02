package io.github.subjekt.utils

import io.github.subjekt.nodes.suite.Parameter

object Permutations {

  data class DefinedParameter(
    val identifier: String,
    val value: Any
  )

  fun List<Parameter>.permute(parameterConfigurationConsumer: (List<DefinedParameter>) -> Unit) {
    val cartesianProduct = this.map { it.values }.fold(sequenceOf(emptyList<Any>())) { acc, values ->
      acc.flatMap { combination ->
        values.asSequence().map { combination + it }
      }
    }

    cartesianProduct.map { combination ->
      combination.mapIndexed { index, value ->
        DefinedParameter(this[index].name, value)
      }
    }.forEach {
        parameterConfigurationConsumer(it)
    }
  }

  fun <T> Iterable<Iterable<T>>.permute(): Iterable<Iterable<T>> {
    return fold(listOf(emptyList<T>()) as Iterable<List<T>>) { acc, iterable ->
      acc.flatMap { combination ->
        iterable.map { element ->
          combination + element
        }
      }
    }
  }

}
