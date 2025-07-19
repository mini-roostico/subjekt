/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.permutations

import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.SymbolTable.Companion.ARGS_SEPARATOR
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.core.definition.DefinedParameter
import io.github.subjekt.core.value.Value.Companion.asStringValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class PermutationUtilsSpec : StringSpec({

    "contextPermutationsOutOf should return all the possible contexts" {
        val parameters =
            setOf(
                listOf(
                    DefinedParameter("param1", "value1".asStringValue()),
                    DefinedParameter("param1", "value2".asStringValue()),
                ),
                listOf(
                    DefinedParameter("param2", "value3".asStringValue()),
                    DefinedParameter("param2", "value4".asStringValue()),
                ),
            )
        val macros =
            setOf(
                listOf(
                    DefinedMacro("macro1", listOf("arg1"), Resolvable("body1")),
                    DefinedMacro("macro1", listOf("arg1"), Resolvable("body2")),
                ),
                listOf(
                    DefinedMacro("macro2", listOf("arg2"), Resolvable("body3")),
                    DefinedMacro("macro2", listOf("arg2"), Resolvable("body4")),
                ),
            )
        val functions = setOf(SubjektFunction("func1") { it.first() }, SubjektFunction("func2") { it.last() })
        val symbolTable = SymbolTable()
        val contexts = symbolTable.contextPermutationsOutOf(parameters, macros, functions)
        val expectedSet =
            listOf("1", "2")
                .flatMap { firstParamNum ->
                    listOf("3", "4").flatMap { secondParamNum ->
                        listOf("1", "2").flatMap { firstMacroNum ->
                            listOf("3", "4").map { secondMacroNum ->
                                Context(
                                    mapOf(
                                        "param1" to DefinedParameter("param1", "value$firstParamNum".asStringValue()),
                                        "param2" to DefinedParameter("param2", "value$secondParamNum".asStringValue()),
                                    ),
                                    mapOf(
                                        "macro1" + ARGS_SEPARATOR + "1" to
                                            DefinedMacro("macro1", listOf("arg1"), Resolvable("body$firstMacroNum")),
                                        "macro2" + ARGS_SEPARATOR + "1" to
                                            DefinedMacro("macro2", listOf("arg2"), Resolvable("body$secondMacroNum")),
                                    ),
                                    mapOf(
                                        "func1" to SubjektFunction("func1") { it.first() },
                                        "func2" to SubjektFunction("func2") { it.last() },
                                    ),
                                    symbolTable,
                                )
                            }
                        }
                    }
                }.toSet()
        contexts.toSet() shouldBe expectedSet
    }

    "contextPermutationsOutOf should return all the possible contexts with empty parameters, macros, and functions" {
        val symbolTable = SymbolTable()
        val contexts = symbolTable.contextPermutationsOutOf(emptySet(), emptySet(), emptySet())
        contexts shouldBe listOf(Context(originalSymbolTable = symbolTable))
    }

    "contextPermutationsOutOf should return all the possible context with possibly single parameters" {
        val parameters =
            setOf(
                listOf(
                    DefinedParameter("param1", "value1".asStringValue()),
                ),
                listOf(
                    DefinedParameter("param2", "value3".asStringValue()),
                    DefinedParameter("param2", "value4".asStringValue()),
                ),
            )
        val macros =
            setOf(
                listOf(
                    DefinedMacro("macro", listOf("arg1"), Resolvable("body1")),
                ),
            )
        val symbolTable = SymbolTable()
        val contexts = symbolTable.contextPermutationsOutOf(parameters, macros, emptySet())
        val expectedSet =
            listOf("3", "4")
                .map { value ->
                    Context(
                        mapOf(
                            "param1" to DefinedParameter("param1", "value1".asStringValue()),
                            "param2" to DefinedParameter("param2", "value$value".asStringValue()),
                        ),
                        mapOf(
                            "macro${ARGS_SEPARATOR}1" to DefinedMacro("macro", listOf("arg1"), Resolvable("body1")),
                        ),
                        emptyMap(),
                        symbolTable,
                    )
                }.toSet()
        contexts.toSet() shouldBe expectedSet
    }
})
