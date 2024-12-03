package io.github.subjekt.resolved

import io.github.subjekt.nodes.Context
import io.github.subjekt.utils.MessageCollector

interface Resolvable {

  fun resolveOne(context: Context, messageCollector: MessageCollector): String

  fun resolve(context: Context, messageCollector: MessageCollector): Iterable<String>

  val source: String
}
