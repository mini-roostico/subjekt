package io.github.subjekt

import io.github.subjekt.SubjektCompiler.compile
import io.github.subjekt.resolved.ResolvedSubject
import io.github.subjekt.resolved.ResolvedSuite
import io.github.subjekt.utils.MessageCollector
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CompilerTests {

  private val collector: MessageCollector = MessageCollector.SimpleCollector()

  @BeforeEach
  fun setUp() {
    collector.flushMessages()
  }

  private fun ResolvedSuite.toCode(): Set<String> = this.subjects.map(ResolvedSubject::code).toSet()

  @Test
  fun `Simple YAML`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |  - name: Test subject
      |    code: |-
      |      Subject code here!
      |    outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("Subject code here!"), generated)
  }

  @Test
  fun `Incomplete YAML - missing outcomes`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  code: Test code
      """.trimMargin(),
      collector,
    )
    assertEquals(1, collector.messages.size)
    assertNull(generated)
  }

  @Test
  fun `Incomplete YAML - missing code`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  outcomes: []
      """.trimMargin(),
      collector,
    )
    assertEquals(1, collector.messages.size)
    assertNull(generated)
  }

  @Test
  fun `Incomplete YAML - missing name`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- code: Test code
      |  outcomes: []
      """.trimMargin(),
      collector,
    )
    assertEquals(1, collector.messages.size)
    assertNull(generated)
  }

  @Test
  fun `Suite with macros`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |macros:
      |- def: macro(a)
      |  values:
      |  - "(${"\${{a}}"})"
      |  - "{${"\${{a}}"}}"
      |subjects:
      |- name: Test subject
      |  code: ${"\${{macro(\"test\")}}"}
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("(test)", "{test}"), generated)
  }

  @Test
  fun `Suite with nested macros`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |macros:
      |- def: macro(a)
      |  values:
      |  - "(${"\${{ a }}"})"
      |  - "{${"\${{ a }}"}}"
      |- def: nested(a)
      |  values:
      |  - "1${"\${{ a }}"}1"
      |  - "2${"\${{ a }}"}2"
      |subjects:
      |- name: Test subject
      |  code: ${"\${{ macro(nested(\"test\")) }}"}
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("(1test1)", "(2test2)", "{1test1}", "{2test2}"), generated)
  }

  @Test
  fun `Suite with parameters and macros`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |parameters:
      |- name: test
      |  values:
      |  - "a"
      |  - "b"
      |macros:
      |- def: macro(a)
      |  values:
      |  - "(${"\${{a}}"})"
      |  - "{${"\${{a}}"}}"
      |subjects:
      |- name: Test subject
      |  code: ${"\${{macro(test)}}"}
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("(a)", "{a}", "(b)", "{b}"), generated)
  }

  @Test
  fun `Suite with custom expression delimiters`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |config:
      |  expressionPrefix: "%%"
      |  expressionSuffix: "%%"
      |macros:
      |  - def: macro(a)
      |    values:
      |    - "(%%a%%)"
      |    - "{%%a%%}"
      |subjects:
      |- name: Test subject
      |  code: "%%macro('\"test\"')%%"
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("(\"test\")", "{\"test\"}"), generated)
  }
}
