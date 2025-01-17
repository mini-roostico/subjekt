/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SymbolTableSpec : StringSpec({

    "SymbolTable should combine parameters correctly" {
        val param1 = Parameter("param1", listOf("value1"))
        val param2 = Parameter("param2", listOf("value2"))
        val table1 = SymbolTable(parameters = mapOf(param1.id to param1))
        val table2 = SymbolTable(parameters = mapOf(param2.id to param2))

        val combinedTable = table1 + table2

        combinedTable.resolveParameter("param1") shouldBe param1
        combinedTable.resolveParameter("param2") shouldBe param2
    }

    "SymbolTable should combine macros correctly" {
        val macro1 = Macro("macro1", listOf("arg1"), listOf(Resolvable("body1")))
        val macro2 = Macro("macro2", listOf("arg2"), listOf(Resolvable("body2")))
        val table1 = SymbolTable(macros = mapOf(macro1.id to macro1))
        val table2 = SymbolTable(macros = mapOf(macro2.id to macro2))

        val combinedTable = table1 + table2

        combinedTable.resolveMacro("macro1") shouldBe macro1
        combinedTable.resolveMacro("macro2") shouldBe macro2
    }

    "SymbolTable should combine functions correctly" {
        val function1: Function1<List<*>, List<*>> = { it }
        val function2: Function1<List<*>, List<*>> = { it.reversed() }
        val table1 = SymbolTable(functions = mapOf("func1" to function1))
        val table2 = SymbolTable(functions = mapOf("func2" to function2))

        val combinedTable = table1 + table2

        combinedTable.resolveFunction("func1") shouldBe function1
        combinedTable.resolveFunction("func2") shouldBe function2
    }

    "SymbolTable should define and resolve parameters correctly" {
        val param = Parameter("param", listOf("value"))
        val table = SymbolTable().defineParameter(param)

        table.resolveParameter("param") shouldBe param
    }

    "SymbolTable should define and resolve macros correctly" {
        val macro = Macro("macro", listOf("arg"), listOf(Resolvable("body")))
        val table = SymbolTable().defineMacro(macro)

        table.resolveMacro("macro") shouldBe macro
    }

    "SymbolTable should define and resolve functions correctly" {
        val function: Function1<List<*>, List<*>> = { it }
        val table = SymbolTable().defineFunction("func", function)

        table.resolveFunction("func") shouldBe function
    }
})
