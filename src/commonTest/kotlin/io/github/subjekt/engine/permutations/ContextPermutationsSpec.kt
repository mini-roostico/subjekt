/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.permutations

import io.github.subjekt.core.Macro
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Subject
import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.core.definition.DefinedParameter
import io.github.subjekt.core.value.Value.Companion.asStringValue
import io.github.subjekt.engine.expressions.CallSymbol
import io.github.subjekt.engine.expressions.ParameterSymbol
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe

class ContextPermutationsSpec : StringSpec({
    "requestNeededContexts should return only one empty context when there are no expressions" {
        val initialSymbolTable = SymbolTable()
        val subject =
            Subject(
                id = 0,
                resolvables =
                    mapOf(
                        "name" to Resolvable("resolvable1"),
                        "body" to Resolvable("resolvable2"),
                    ),
                symbolTable = initialSymbolTable,
            )

        val result = subject.requestNeededContexts()

        result shouldBe listOf(Context(originalSymbolTable = initialSymbolTable))
    }

    "requestNeededContexts should return all the needed contexts for the given subject" {
        val initialSymbolTable =
            SymbolTable()
                .defineParameter(Parameter("param1", listOf("value1".asStringValue(), "value2".asStringValue())))
                .defineParameter(Parameter("param2", listOf("value3".asStringValue(), "value4".asStringValue())))
                .defineParameter(Parameter("param3", listOf("value5".asStringValue())))
                .defineParameter(Parameter("shouldNotBeIncluded", listOf("value6".asStringValue())))
                .defineMacro(
                    Macro("macro1", listOf("arg1"), listOf(Resolvable("\${{ param3 }}"))),
                ).defineMacro(
                    Macro("notIncluded", listOf("arg2"), listOf(Resolvable("\${{ param4 }}"))),
                ).defineFunction("fun") { it.first() }
        val subject =
            Subject(
                id = 0,
                resolvables =
                    mapOf(
                        "name" to Resolvable("\${{ param1 }} + \${{ macro1(param1) }}"),
                        "body" to Resolvable("\${{ param2 }} rest of the body \${{ fun(param1) }}"),
                    ),
                symbolTable = initialSymbolTable,
            )

        val result = subject.requestNeededContexts()

        val expectedContexts =
            listOf("1", "2")
                .flatMap { valueParam1 ->
                    listOf("3", "4").map { valueParam2 ->
                        Context(originalSymbolTable = initialSymbolTable)
                            .withParameters(
                                "param1" to "value$valueParam1".asStringValue(),
                                "param2" to "value$valueParam2".asStringValue(),
                                "param3" to "value5".asStringValue(),
                            ).withMacro(
                                "macro1",
                                listOf("arg1"),
                                Resolvable("\${{ param3 }}"),
                            ).withFunction("fun") { it.first() }
                    }
                }.toSet()

        result.toSet() shouldBe expectedContexts
    }

    "populateDefinedSymbols should populate parameters, macros, and functions correctly" {
        val symbolTable =
            SymbolTable().defineParameter(
                Parameter("internal1", listOf("valueInternal1".asStringValue())),
            )
        val parameters = mutableSetOf<List<DefinedParameter>>()
        val macros = mutableSetOf<List<DefinedMacro>>()
        val functions = mutableSetOf<SubjektFunction>()

        val parameter = Parameter("param1", listOf("value1".asStringValue(), "value2".asStringValue()))
        parameter.populateDefinedSymbols(symbolTable, parameters, macros, functions)
        parameters shouldContain
            listOf(
                DefinedParameter("param1", "value1".asStringValue(), parameter),
                DefinedParameter("param1", "value2".asStringValue(), parameter),
            )
        macros shouldBe emptySet()
        functions shouldBe emptySet()

        val macro = Macro("macro1", listOf("arg1"), listOf(Resolvable("\${{ internal1 }}"), Resolvable("resolvable2")))
        macro.populateDefinedSymbols(symbolTable, parameters, macros, functions)

        parameters shouldContain
            listOf(
                DefinedParameter("param1", "value1".asStringValue()),
                DefinedParameter("param1", "value2".asStringValue()),
            )
        parameters shouldContain listOf(DefinedParameter("internal1", "valueInternal1".asStringValue()))
        macros shouldContain
            listOf(
                DefinedMacro("macro1", listOf("arg1"), Resolvable("\${{ internal1 }}")),
                DefinedMacro("macro1", listOf("arg1"), Resolvable("resolvable2")),
            )
    }

    "toDefinedParameters should return correct defined parameters" {
        val parameter = Parameter("param1", listOf("value1".asStringValue(), "value2".asStringValue()))

        val result = parameter.toDefinedParameters()

        result shouldBe
            listOf(
                DefinedParameter("param1", "value1".asStringValue()),
                DefinedParameter("param1", "value2".asStringValue()),
            )
    }

    "toDefinedMacros should return correct defined macros" {
        val macro = Macro("macro1", listOf("arg1"), listOf(Resolvable("resolvable1"), Resolvable("resolvable2")))

        val result = macro.toDefinedMacros()

        result shouldBe
            listOf(
                DefinedMacro(
                    "macro1",
                    listOf("arg1"),
                    Resolvable("resolvable1"),
                ),
                DefinedMacro(
                    "macro1",
                    listOf("arg1"),
                    Resolvable("resolvable2"),
                ),
            )
    }

    "extractNeededSymbols should return correct resolvable symbols" {
        val macro = Macro("macro1", listOf("arg1"), listOf(Resolvable("\${{ par1 + mac(par1) }}")))
        val result = macro.extractNeededSymbols()
        result shouldBe setOf(ParameterSymbol("par1"), CallSymbol("mac", 1))
    }
})
