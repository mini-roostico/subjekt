package io.github.subjekt.nodes.suite

/**
 * Represents a parameter node.
 */
data class Parameter(
  /**
   * The identifier of the parameter.
   */
  val name: String,
  /**
   * The list of values of the parameter. Each parameter can have multiple values, but these can't be resolvable.
   * Instead, they must be already defined values.
   */
  val values: List<Any>,
) {

  companion object {
    /**
     * Creates a Parameter node from a YAML [parameter] parsed data class.
     */
    fun fromYamlParameter(parameter: io.github.subjekt.yaml.Parameter): Parameter {
      if (parameter.values == null && parameter.value == null) {
        throw IllegalArgumentException("Illegal parameter definition. Expected 'values' or 'value' in $parameter")
      }
      return Parameter(parameter.name, parameter.values ?: listOf(parameter.value!!))
    }
  }
}
