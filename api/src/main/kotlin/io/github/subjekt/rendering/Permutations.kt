package io.github.subjekt.rendering

import io.github.subjekt.files.Parameter

object Permutations {
  fun List<List<String>>.permute(separator: String = ""): List<String> {
    if (isEmpty()) return emptyList()
    val result = mutableListOf<String>()
    generatePermutationsHelper(this, 0, "", result, separator)
    return result
  }

  private fun generatePermutationsHelper(
    lists: List<List<String>>,
    depth: Int,
    current: String,
    result: MutableList<String>,
    separator: String = ""
  ) {
    if (depth == lists.size) {
      result.add(current)
      return
    }
    for (item in lists[depth]) {
      generatePermutationsHelper(
        lists,
        depth + 1,
        if (current == "") item else current + separator + item,
        result,
        separator
      )
    }
  }

  fun List<Parameter>.permute(): List<Map<String, Any>> =
    mutableListOf<Map<String, Any>>()
      .also { generatePermutationsHelper(this, 0, mutableMapOf(), it) }

  private fun generatePermutationsHelper(
    parameters: List<Parameter>,
    depth: Int,
    currentMap: MutableMap<String, Any>,
    result: MutableList<Map<String, Any>>
  ) {
    // Base case: If we've reached the last parameter, add the current combination to results
    if (depth == parameters.size) {
      result.add(currentMap.toMap()) // toMap() to ensure immutability
      return
    }

    // Get the current parameter and iterate over its values
    val parameter = parameters[depth]
    for (value in parameter.values) {
      // Add the parameter value to the map
      currentMap[parameter.name] = value
      // Recurse to the next parameter
      generatePermutationsHelper(parameters, depth + 1, currentMap, result)
    }
  }

}
