/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.core.value.Value.Companion.asStringValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SymbolTableSpec : StringSpec({

    "SymbolTable should combine parameters correctly" {
        val param1 = Parameter("param1", listOf("value1".asStringValue()))
        val param2 = Parameter("param2", listOf("value2".asStringValue()))
        val table1 = SymbolTable(parameters = mapOf(param1.id to param1))
        val table2 = SymbolTable(parameters = mapOf(param2.id to param2))

        val combinedTable = table1 + table2

        combinedTable.resolveParameter("param1") shouldBe param1
        combinedTable.resolveParameter("param2") shouldBe param2
    }

    "SymbolTable should combine macros correctly" {
        val macro1 = Macro("macro1", listOf("arg1"), listOf(Resolvable("body1")))
        val macro2 = Macro("macro2", listOf("arg2"), listOf(Resolvable("body2")))
        val table1 = SymbolTable().defineMacro(macro1)
        val table2 = SymbolTable().defineMacro(macro2)

        val combinedTable = table1 + table2
        combinedTable.resolveMacro("macro1", 1) shouldBe macro1
        combinedTable.resolveMacro("macro2", 1) shouldBe macro2
    }

    "SymbolTable should combine functions correctly" {
        val function1: Function1<List<String>, String> = { it.first() }
        val function2: Function1<List<String>, String> = { it.last() }
        val table1 = SymbolTable().defineStringFunction("func1", function1)
        val table2 = SymbolTable().defineStringFunction("func2", function2)

        val combinedTable = table1 + table2

        combinedTable.resolveFunction("func1") shouldBe SubjektFunction.fromStringFunction("func1", function1)
        combinedTable.resolveFunction("func2") shouldBe SubjektFunction.fromStringFunction("func2", function2)
    }

    "SymbolTable should define and resolve parameters correctly" {
        val param = Parameter("param", listOf("value".asStringValue()))
        val table = SymbolTable().defineParameter(param)

        table.resolveParameter("param") shouldBe param
    }

    "SymbolTable should define and resolve macros correctly" {
        val macro = Macro("macro", listOf("arg"), listOf(Resolvable("body")))
        val table = SymbolTable().defineMacro(macro)

        table.resolveMacro("macro", 1) shouldBe macro
    }

    "SymbolTable should define and resolve functions correctly" {
        val function: Function1<List<String>, String> = { it.first() }
        val table = SymbolTable().defineStringFunction("func", function)

        table.resolveFunction("func") shouldBe SubjektFunction.fromStringFunction("func", function)
    }
})
