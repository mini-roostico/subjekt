package io.github.subjekt.yaml

import org.intellij.lang.annotations.Language
import kotlin.test.Test
import kotlin.test.assertEquals

class ListHandlingTest {

  @Test
  fun `should deserialize list of strings`() {
    val yaml = """
      - one
      - two
      - three
    """.trimIndent()

    val result = Reader.readYaml<List<String>>(yaml)

    assertEquals(listOf("one", "two", "three"), result)
  }

  @Test
  fun `should deserialize list of integers`() {
    val yaml = """
      - 1
      - 2
      - 3
    """.trimIndent()

    val result = Reader.readYaml<List<Int>>(yaml)

    assertEquals(listOf(1, 2, 3), result)
  }

  @Test
  fun `should deserialize a single element list without dash`() {
    val yamlSugar = """
      name: "suite"
      subjects:
        name: "subject"
        code: "this is code"
    """.trimIndent()
    val yaml = """
      name: "suite"
      subjects:
        - name: "subject"
          code: "this is code"
    """.trimIndent()

    val resultSugar = Reader.suiteFromYaml(yamlSugar)
    val result = Reader.suiteFromYaml(yaml)

    assertEquals(
      Suite("suite", null, null, listOf(Subject("subject", null, null, "this is code", null, null)), null),
      resultSugar,
    )
    assertEquals(
      result,
      resultSugar,
    )
  }

  @Test
  fun `should deserialize a full suite with syntactic sugar`() {
    @Language("YAML")
    val yaml = """
      name: "suite"
      macros:
        def: "macro(param1, param2)"
        values:
          - "value1"
          - "value2"
      subjects:
        - name: "subject"
          parameters:
            - name: "param"
              values:
                - "value"
            - name: "param2"
              value: "value2"
            - name: "param3"
              values: "value3"
          macros:
            - def: "macro(param1)"
              value: "value"
          code: "this is code"
          outcomes:
            - warning: "warning"
            - error: "error"
          properties:
            key: "value"
    """.trimIndent()

    val result = Reader.suiteFromYaml(yaml)

    assertEquals(
      Suite(
        "suite",
        null,
        listOf(
          Macro("macro(param1, param2)", listOf("value1", "value2"), null),
        ),
        listOf(
          Subject(
            "subject",
            listOf(
              Parameter("param", listOf("value"), null),
              Parameter("param2", null, "value2"),
              Parameter("param3", listOf("value3"), null),
            ),
            listOf(
              Macro("macro(param1)", null, "value"),
            ),
            "this is code",
            listOf(
              Outcome("warning", null),
              Outcome(null, "error"),
            ),
            mapOf("key" to "value"),
          ),
        ),
        null,
      ),
      result,
    )
  }
}
