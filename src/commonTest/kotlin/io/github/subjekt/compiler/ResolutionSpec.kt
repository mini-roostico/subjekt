/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler

import io.github.subjekt.core.Macro
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Subject
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.parsing.SuiteFactory.SubjectBuilder
import io.github.subjekt.core.parsing.SuiteFactory.SuiteBuilder
import io.github.subjekt.core.resolution.Instance
import io.github.subjekt.core.resolution.ResolvedSubject
import io.github.subjekt.core.resolution.ResolvedSuite
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class ResolutionSpec : StringSpec({

    fun createSuite(
        id: String,
        vararg subjects: Subject,
        symbolTable: SymbolTable = SymbolTable(),
    ) = SuiteBuilder()
        .id(id)
        .subjects(subjects.toList())
        .symbolTable(symbolTable)
        .build()

    fun createSubject(
        id: Int,
        name: Resolvable,
        symbolTable: SymbolTable = SymbolTable(),
    ): Subject =
        SubjectBuilder()
            .id(id)
            .name(name)
            .symbolTable(symbolTable)
            .build()

    "resolution of a trivial Suite should work" {
        val resolvable = Resolvable("test")
        val suite = createSuite("test", createSubject(0, resolvable))
        val expected =
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("test", resolvable)),
                    ),
                ),
            )
        suite.resolve() shouldBe expected
    }

    "resolution of a Suite with parameters should work" {
        val resolvable = Resolvable("Test number: \${{ test }}")
        val symbolTable = SymbolTable().defineParameter(Parameter("test", listOf("1", "2")))
        val suite =
            createSuite(
                "test",
                createSubject(0, resolvable, symbolTable),
            )
        val expected =
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: 1", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: 2", resolvable)),
                    ),
                ),
            )
        suite.resolve() shouldBe expected
    }

    "resolution of a Suite with macros should work" {
        val resolvable = Resolvable("Test number: \${{ test() }}")
        val symbolTable =
            SymbolTable()
                .defineMacro(
                    Macro(
                        "test",
                        emptyList(),
                        listOf(
                            Resolvable("1"),
                            Resolvable("2"),
                            Resolvable("3"),
                        ),
                    ),
                )
        val suite = createSuite("test", createSubject(0, resolvable, symbolTable))
        suite.resolve() shouldBe
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: 1", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: 2", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: 3", resolvable)),
                    ),
                ),
            )
    }

    "resolution of a Suite with macros and parameters should work" {
        val resolvable = Resolvable("Test number: \${{ test(param) }}")
        val symbolTable =
            SymbolTable()
                .defineMacro(
                    Macro(
                        "test",
                        listOf("param"),
                        listOf(Resolvable("(\${{ param }})")),
                    ),
                ).defineParameter(Parameter("param", listOf("1", "2", "3")))
        val suite = createSuite("test", createSubject(0, resolvable, symbolTable))
        suite.resolve() shouldBe
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: (1)", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: (2)", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: (3)", resolvable)),
                    ),
                ),
            )
    }

    "resolution of a Suite with overloaded macros using parameters should work" {
        val resolvable = Resolvable("Test number: \${{ test() .. test('1')}}")
        val symbolTable =
            SymbolTable()
                .defineMacro(
                    Macro(
                        "test",
                        emptyList(),
                        listOf(
                            Resolvable("(\${{ param }})"),
                        ),
                    ),
                ).defineMacro(
                    Macro(
                        "test",
                        listOf("arg"),
                        listOf(
                            Resolvable("(\${{ arg }})"),
                        ),
                    ),
                ).defineParameter(
                    Parameter("param", listOf("1", "2", "3")),
                )
        val suite = createSuite("test", createSubject(0, resolvable, symbolTable))
        suite.resolve() shouldBe
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: (1)(1)", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: (2)(1)", resolvable)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: (3)(1)", resolvable)),
                    ),
                ),
            )
    }

    "resolution of a Suite with multiple subjects should work" {
        val resolvable1 = Resolvable("Test number: \${{ param .. test(param2)}}")
        val resolvable2 = Resolvable("Test number: \${{ param .. test(param2)}}")
        val symbolTable1 =
            SymbolTable()
                .defineMacro(
                    Macro(
                        "test",
                        listOf("arg"),
                        listOf(
                            Resolvable("(\${{ arg }})"),
                        ),
                    ),
                ).defineParameters(
                    listOf(
                        Parameter("param", listOf("- ", "* ")),
                        Parameter("param2", listOf("1", "2")),
                    ),
                )
        val symbolTable2 =
            SymbolTable()
                .defineMacro(
                    Macro(
                        "test",
                        listOf("arg"),
                        listOf(
                            Resolvable("{\${{ arg }}}"),
                        ),
                    ),
                ).defineParameters(
                    listOf(
                        Parameter("param", listOf("[] ", ". ")),
                        Parameter("param2", listOf("1", "2")),
                    ),
                )
        val suite =
            createSuite(
                "test",
                createSubject(0, resolvable1, symbolTable1),
                createSubject(1, resolvable2, symbolTable2),
            )
        val result = suite.resolve()
        println(result.resolvedSubjects.joinToString(separator = "\n") { it.name?.value ?: "" })
        result shouldBe
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: - (1)", resolvable1)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: * (1)", resolvable1)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: - (2)", resolvable1)),
                    ),
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: * (2)", resolvable1)),
                    ),
                    ResolvedSubject(
                        1,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: [] {1}", resolvable2)),
                    ),
                    ResolvedSubject(
                        1,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: . {1}", resolvable2)),
                    ),
                    ResolvedSubject(
                        1,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: [] {2}", resolvable2)),
                    ),
                    ResolvedSubject(
                        1,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: . {2}", resolvable2)),
                    ),
                ),
            )
    }

    "resolution of a Suite with functions" {
        val resolvable = Resolvable("Test number: \${{ plus(pi(), '1') }}")
        val symbolTable =
            SymbolTable()
                .defineFunction("pi") { PI.toString() }
                .defineFunction("plus") {
                    require(it.size == 2)
                    (it[0].toDouble() + it[1].toDouble()).toString()
                }
        val suite = createSuite("test", createSubject(0, resolvable, symbolTable))
        suite.resolve() shouldBe
            ResolvedSuite(
                suite,
                setOf(
                    ResolvedSubject(
                        0,
                        mapOf(Subject.DEFAULT_NAME_KEY to Instance("Test number: ${PI + 1}", resolvable)),
                    ),
                ),
            )
    }
})
