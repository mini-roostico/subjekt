/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.visitors

import io.github.subjekt.compiler.conversion.Stdlib
import io.github.subjekt.compiler.nodes.suite.Macro
import io.github.subjekt.compiler.nodes.suite.Outcome
import io.github.subjekt.compiler.nodes.suite.Parameter
import io.github.subjekt.compiler.nodes.suite.Subject
import io.github.subjekt.compiler.nodes.suite.Suite
import io.github.subjekt.compiler.nodes.suite.Template
import io.github.subjekt.compiler.resolved.ResolvedOutcome
import io.github.subjekt.compiler.resolved.ResolvedSubject
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.compiler.visitors.SuiteVisitor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SuiteVisitorTest : StringSpec({
    val collector: MessageCollector = MessageCollector.SimpleCollector()

    fun compile(suite: Suite): MutableSet<ResolvedSubject> {
        val visitor = SuiteVisitor(collector, listOf(Stdlib))
        visitor.visitSuite(suite)
        return visitor.resolvedSubjects
    }

    fun getExampleSuite(): Suite =
        Suite(
            "Test suite",
            macros =
                listOf(
                    Macro(
                        "macro1",
                        listOf("a"),
                        listOf(Template.parse("Foo1: \${{a}}"), Template.parse("Foo2: \${{a}}")),
                    ),
                    Macro(
                        "macro2",
                        listOf("a"),
                        listOf(Template.parse("Bar1: \${{a}}"), Template.parse("Bar2: \${{a}}")),
                    ),
                ),
            subjects =
                listOf(
                    Subject(
                        Template.parse("Test Subject"),
                        code = Template.parse("Generated: \${{ macro1(\"1\") }} \${{ macro2(\"2\") }}"),
                    ),
                ),
        )

    "Simple suite".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                listOf(
                    Subject(
                        Template.parse("Subject 1"),
                        code = Template.parse("This is the test code"),
                    ),
                ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Suite with parameters".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                parameters =
                    listOf(
                        Parameter("a", listOf("1", "2")),
                        Parameter("b", listOf("3", "4")),
                    ),
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject 1"),
                            code = Template.parse("This is the test code \${{a}} \${{b}}"),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code 1 3", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code 2 3", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code 1 4", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code 2 4", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Suite with outcomes".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject 1"),
                            code = Template.parse("This is the test code"),
                            outcomes =
                                listOf(
                                    Outcome.Warning(
                                        Template.parse("This is a warning"),
                                    ),
                                ),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject(
                    "Subject 1",
                    "This is the test code",
                    listOf(ResolvedOutcome.Warning("This is a warning")),
                ),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Suite with macros".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                macros =
                    listOf(
                        Macro(
                            "macro1",
                            listOf("a"),
                            listOf(Template.parse("This is the test code \${{a}}")),
                        ),
                        Macro(
                            "macroName",
                            listOf(),
                            listOf(Template.parse("GeneratedName")),
                        ),
                    ),
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject\${{ macroName() }}"),
                            code = Template.parse("\${{macro1(\"1\")}}"),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("SubjectGeneratedName", "This is the test code 1", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Suite with permutating macros".config(enabled = false) {
        val suite = getExampleSuite()
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Test Subject", "Generated: Foo1: Bar1: 1", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo1: Bar2: 1", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: Bar1: 1", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: Bar2: 1", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Multiple macros with same arguments".config(enabled = false) {
        val suite = getExampleSuite()
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Test Subject", "Generated: Foo1: 1 Bar1: 2", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo1: 1 Bar2: 2", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: 1 Bar1: 2", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: 1 Bar2: 2", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Suite with multiple subjects".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject 1"),
                            code = Template.parse("This is the test code"),
                        ),
                        Subject(
                            Template.parse("Subject 2"),
                            code = Template.parse("This is the test code"),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code", emptyList()),
                ResolvedSubject("Subject 2", "This is the test code", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    fun exampleParameter(): List<Parameter> = listOf(Parameter("a", listOf("1", "2")))

    "Subject with unresolved macro".config(enabled = false) {
        val testSubject =
            Subject(
                Template.parse("Subject 1"),
                code = Template.parse("This is the test code \${{macro1()}}"),
                macros = listOf(Macro("macro1", listOf(), listOf(Template.parse("Macro(\${{a}})")))),
            )

        val suite = Suite("Test suite", parameters = exampleParameter(), subjects = listOf(testSubject))
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code Macro(1)", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code Macro(2)", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Subject with unresolved, multiple-valued macro".config(enabled = false) {
        val testSubject =
            Subject(
                Template.parse("Subject 1"),
                code = Template.parse("This is the test code \${{macro1()}}"),
                macros =
                    listOf(
                        Macro(
                            "macro1",
                            listOf(),
                            listOf(
                                Template.parse("(\${{a}})"),
                                Template.parse("{\${{a}}}"),
                            ),
                        ),
                    ),
            )
        val suite =
            Suite(
                "Test suite",
                parameters = exampleParameter(),
                subjects = listOf(testSubject),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code (1)", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code {1}", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code (2)", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code {2}", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Subject with unresolved, multiple-valued macro and arguments".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                parameters =
                    listOf(
                        Parameter("a", listOf("1", "2")),
                        Parameter("b", listOf("3", "4")),
                        Parameter("c", listOf("3", "4")),
                    ),
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject 1"),
                            code = Template.parse("Test code: \${{macro1(c)}}"),
                            macros =
                                listOf(
                                    Macro(
                                        "macro1",
                                        listOf("a"),
                                        listOf(
                                            Template.parse("(\${{a}}, \${{b}})"),
                                            Template.parse("{\${{a}}, \${{b}}}"),
                                        ),
                                    ),
                                ),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "Test code: (3, 3)", emptyList()),
                ResolvedSubject("Subject 1", "Test code: (4, 3)", emptyList()),
                ResolvedSubject("Subject 1", "Test code: (3, 4)", emptyList()),
                ResolvedSubject("Subject 1", "Test code: (4, 4)", emptyList()),
                ResolvedSubject("Subject 1", "Test code: {3, 3}", emptyList()),
                ResolvedSubject("Subject 1", "Test code: {4, 3}", emptyList()),
                ResolvedSubject("Subject 1", "Test code: {3, 4}", emptyList()),
                ResolvedSubject("Subject 1", "Test code: {4, 4}", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }

    "Simple stdlib call".config(enabled = false) {
        val suite =
            Suite(
                "Test suite",
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject 1"),
                            code = Template.parse("\${{std.capitalizeFirst(\"test\")}}"),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "Test", emptyList()),
            )
        collector.hasErrors() shouldBe false
        subjects shouldBe expected
    }
})
