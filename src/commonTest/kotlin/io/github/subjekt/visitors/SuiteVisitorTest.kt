/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.visitors

import io.github.subjekt.conversion.Stdlib
import io.github.subjekt.nodes.suite.Macro
import io.github.subjekt.nodes.suite.Outcome
import io.github.subjekt.nodes.suite.Parameter
import io.github.subjekt.nodes.suite.Subject
import io.github.subjekt.nodes.suite.Suite
import io.github.subjekt.nodes.suite.Template
import io.github.subjekt.resolved.ResolvedOutcome
import io.github.subjekt.resolved.ResolvedSubject
import io.github.subjekt.utils.MessageCollector
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SuiteVisitorTest {
    private val collector: MessageCollector = MessageCollector.SimpleCollector()

    private fun compile(suite: Suite): MutableSet<ResolvedSubject> {
        val visitor = SuiteVisitor(collector, listOf(Stdlib))
        visitor.visitSuite(suite)
        return visitor.resolvedSubjects
    }

    @Test
    fun `Simple suite`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Suite with parameters`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Suite with outcomes`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Suite with macros`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Suite with permutating macros`() {
        val suite =
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
                            code = Template.parse("Generated: \${{ macro1(macro2(\"1\")) }}"),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Test Subject", "Generated: Foo1: Bar1: 1", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo1: Bar2: 1", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: Bar1: 1", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: Bar2: 1", emptyList()),
            )
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Multiple macros with same arguments`() {
        val suite =
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
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Test Subject", "Generated: Foo1: 1 Bar1: 2", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo1: 1 Bar2: 2", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: 1 Bar1: 2", emptyList()),
                ResolvedSubject("Test Subject", "Generated: Foo2: 1 Bar2: 2", emptyList()),
            )
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Suite with multiple subjects`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Subject with unresolved macro`() {
        val suite =
            Suite(
                "Test suite",
                parameters = listOf(Parameter("a", listOf("1", "2"))),
                subjects =
                    listOf(
                        Subject(
                            Template.parse("Subject 1"),
                            code = Template.parse("This is the test code \${{macro1()}}"),
                            macros = listOf(Macro("macro1", listOf(), listOf(Template.parse("Macro(\${{a}})")))),
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code Macro(1)", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code Macro(2)", emptyList()),
            )
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Subject with unresolved, multiple-valued macro`() {
        val suite =
            Suite(
                "Test suite",
                parameters = listOf(Parameter("a", listOf("1", "2"))),
                subjects =
                    listOf(
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
                        ),
                    ),
            )
        val subjects = compile(suite)
        val expected =
            mutableSetOf(
                ResolvedSubject("Subject 1", "This is the test code (1)", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code {1}", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code (2)", emptyList()),
                ResolvedSubject("Subject 1", "This is the test code {2}", emptyList()),
            )
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Subject with unresolved, multiple-valued macro and arguments`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }

    @Test
    fun `Simple stdlib call`() {
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
        assert(!collector.hasErrors())
        assertEquals(expected, subjects)
    }
}
