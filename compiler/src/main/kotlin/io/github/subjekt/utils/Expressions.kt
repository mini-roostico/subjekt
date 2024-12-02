package io.github.subjekt.utils

import io.github.subjekt.nodes.Context

object Expressions {

  fun String.evaluate(context: Context): List<String> =
    listOf(context.parameterSnapshot().entries.fold(this) { acc, entry ->
      acc.replace(entry.key, entry.value.toString())
      TODO()
    })
}
