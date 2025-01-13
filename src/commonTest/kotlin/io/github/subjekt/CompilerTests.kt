/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.github.subjekt.compiler.SubjektCompiler.compile
import io.github.subjekt.compiler.resolved.ResolvedSubject
import io.github.subjekt.compiler.resolved.ResolvedSuite
import io.github.subjekt.compiler.utils.MessageCollector
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class CompilerTests : StringSpec({
    val collector: MessageCollector = MessageCollector.SimpleCollector()

    beforeTest {
        collector.flushMessages()
    }

    fun ResolvedSuite.toCode(): Set<String> = this.subjects.map(ResolvedSubject::code).toSet()

    "Simple YAML" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("Subject code here!")
    }

    "Incomplete YAML - missing code" {
        val generated =
            compile(
                """
            |---
            |name: Test suite
            |subjects:
            |- name: Test subject
            |  outcomes: []
                """.trimMargin(),
                collector,
            )
        collector.hasErrors() shouldBe true
        generated shouldBe null
    }

    "Incomplete YAML - missing name" {
        val generated =
            compile(
                """
            |---
            |name: Test suite
            |subjects:
            |- code: Test code
            |  outcomes: []
                """.trimMargin(),
                collector,
            )
        collector.hasErrors() shouldBe true
        generated shouldBe null
    }

    "Suite with macros" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("(test)", "{test}")
    }

    "Suite with nested macros" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("(1test1)", "(2test2)", "{1test1}", "{2test2}")
    }

    "Suite with parameters and macros" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("(a)", "{a}", "(b)", "{b}")
    }

    "Suite with custom expression delimiters" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("(\"test\")", "{\"test\"}")
    }

    "Suite with wrong dot calls" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe true
        generated?.subjects?.shouldBeEmpty()
    }

    "Missing module" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe true
        collector.messages.shouldContain(
            MessageCollector.Message(
                MessageCollector.MessageType.ERROR,
                "Macro 'b' is not defined in module 'a'",
            ),
        )
        generated?.subjects?.shouldBeEmpty()
    }

    "Missing macro in module" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe true
        collector.messages.shouldContain(
            MessageCollector.Message(
                MessageCollector.MessageType.ERROR,
                "Macro 'b' is not defined in module 'std'",
            ),
        )
        generated?.subjects?.shouldBeEmpty()
    }

    "Inferred call to the standard library" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("Test")
    }

    "Nested call with dot call" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("(Test)", "{Test}")
    }

    "Custom macro vararg argument" {
        val generated =
            compile(
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
        collector.hasErrors() shouldBe false
        generated shouldBe setOf("HelloWorldHowAreYou")
    }

    "Multiple same names resolution" {
        val generatedSubjects =
            compile(
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
                """.trimMargin(),
                collector,
            )
        collector.hasErrors() shouldBe false
        generatedSubjects?.subjects?.size shouldBe 8
        val names = generatedSubjects!!.subjects.map(ResolvedSubject::name).toSet()
        names shouldBe setOf("Test subject1", "Test subject2")
        val generated = generatedSubjects.toCode()
        generated shouldBe
            setOf(
                "(1)[11]",
                "(1)#11#",
                "{1}[11]",
                "{1}#11#",
                "(2)[22]",
                "(2)#22#",
                "{2}[22]",
                "{2}#22#",
            )
    }

    "Subject properties resolution" {
        val generatedSubjects =
            compile(
                """
            |---
            |name: Test suite
            |parameters:
            |- name: test
            |  values: [1, 2]
            |macros:
            |  - def: macro(a)
            |    value: "(${"\${{a}}"})"
            |subjects:
            |  - name: "Test subject${"\${{test}}"}"
            |    macros:
            |      - def: inner(b)
            |        values: ["[${"\${{b + test}}"}]", "#${"\${{b + test}}"}#"]
            |    code: "${"\${{macro(test)}}"}${"\${{inner(test)}}"}"
            |    properties:
            |      prop1: "${"\${{macro(test)}}"}${"\${{inner(test)}}"}"
            |      prop2: "${"\${{inner(test)}}"}${"\${{macro(test)}}"}"
                """.trimMargin(),
                collector,
            )
        collector.hasErrors() shouldBe false
        val properties = generatedSubjects!!.subjects.map { subject -> subject.properties }
        properties shouldBe
            setOf(
                mapOf("prop1" to "(1)[11]", "prop2" to "[11](1)"),
                mapOf("prop1" to "(2)[22]", "prop2" to "[22](2)"),
                mapOf("prop1" to "(1)#11#", "prop2" to "#11#(1)"),
                mapOf("prop1" to "(2)#22#", "prop2" to "#22#(2)"),
            )
    }
})
