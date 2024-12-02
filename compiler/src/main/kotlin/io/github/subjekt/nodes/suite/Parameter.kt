package io.github.subjekt.nodes.suite

data class Parameter(
  val name: String,
  val values: List<Any>,
) {

  companion object {
    fun fromYamlParameter(parameter: io.github.subjekt.yaml.Parameter): Parameter {
      return Parameter(parameter.name, parameter.values)
    }
  }
}
