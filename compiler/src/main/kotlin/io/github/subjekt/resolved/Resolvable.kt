package io.github.subjekt.resolved

import io.github.subjekt.nodes.Context

interface Resolvable {

  fun resolveOne(context: Context): String

  fun resolve(context: Context): Iterable<String>
}
