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
    assert(collector.hasErrors())
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
    assert(collector.hasErrors())
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
    assert(collector.hasErrors())
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

  @Test
  fun `Suite with wrong dot calls`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  code: "${"\${{a.b.c}}"}"
      |  outcomes: []
      """.trimMargin(),
      collector,
    )
    assert(collector.hasErrors())
    assert(generated?.subjects?.isEmpty() == true)
  }

  @Test
  fun `Missing module`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  code: "${"\${{a.b()}}"}"
      |  outcomes: []
      """.trimMargin(),
      collector,
    )
    assert(collector.hasErrors())
    assert(
      collector.messages.first { message -> message.type == MessageCollector.MessageType.ERROR }
        .message.contains("Macro 'b' is not defined in module 'a'"),
    )
    assert(generated?.subjects?.isEmpty() == true)
  }

  @Test
  fun `Missing macro in module`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  code: "${"\${{std.b()}}"}"
      |  outcomes: []
      """.trimMargin(),
      collector,
    )
    assert(collector.hasErrors())
    assert(
      collector.messages.first { message -> message.type == MessageCollector.MessageType.ERROR }
        .message.contains("Macro 'b' is not defined in module 'std'"),
    )
    assert(generated?.subjects?.isEmpty() == true)
  }

  @Test
  fun `Inferred call to the standard library`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  code: ${"\${{capitalizeFirst(\"test\"))}}"}
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("Test"), generated)
  }

  @Test
  fun `Nested call with dot call`() {
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
      |  code: ${"\${{macro(std.capitalizeFirst(\"test\"))}}"}
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("(Test)", "{Test}"), generated)
  }

  @Test
  fun `Custom macro vararg argument`() {
    val generated = compile(
      """
      |---
      |name: Test suite
      |subjects:
      |- name: Test subject
      |  code: ${"\${{std.prettify('hello', 'World', 'how', 'Are', 'you')}}"}
      |  outcomes: []
      """.trimMargin(),
      collector,
    )!!.toCode()
    assert(!collector.hasErrors())
    assertEquals(setOf("HelloWorldHowAreYou"), generated)
  }

  @Test
  fun `Multiple same names resolution`() {
    val generatedSubjects = compile(
      """
      |---
      |name: Test suite
      |parameters:
      |- name: test
      |  values: [1, 2]
      |macros:
      |  - def: macro(a)
      |    values: ["(${"\${{a}}"})", "{${"\${{a}}"}}"]
      |subjects:
      |  - name: "Test subject${"\${{test}}"}"
      |    macros: 
      |      - def: inner(b)
      |        values: ["[${"\${{b + test}}"}]", "#${"\${{b + test}}"}#"]
      |    code: "${"\${{macro(test)}}"}${"\${{inner(test)}}"}"
      |    outcomes: []    
      """.trimMargin(),
      collector,
    )
    assert(!collector.hasErrors())
    assertEquals(8, generatedSubjects?.subjects?.size)
    val names = generatedSubjects!!.subjects.map(ResolvedSubject::name).toSet()
    assertEquals(
      setOf(
        "Test subject1",
        "Test subject2",
      ),
      names.toSet(),
    )
    val generated = generatedSubjects!!.toCode()
    assertEquals(
      setOf(
        "(1)[11]",
        "(1)#11#",
        "{1}[11]",
        "{1}#11#",
        "(2)[22]",
        "(2)#22#",
        "{2}[22]",
        "{2}#22#",
      ),
      generated,
    )
  }
}
