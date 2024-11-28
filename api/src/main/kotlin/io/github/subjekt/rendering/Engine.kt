package io.github.subjekt.rendering

import io.github.subjekt.files.Macro

interface Engine {

  /**
   * Given a [Macro], it returns a list of string representations of all its possible declarations in the template string
   */
  fun renderMacroDeclaration(macro: Macro): List<String>

  /**
   * Renders the given [templateString] using [parametersMap] to substitute variables inside it.
   */
  fun render(templateString: String, parametersMap: Map<String, Any> = mapOf()): String
}
