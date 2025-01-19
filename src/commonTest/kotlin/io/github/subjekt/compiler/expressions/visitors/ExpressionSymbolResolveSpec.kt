/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.visitors

import io.github.subjekt.compiler.expressions.Expression
import io.github.subjekt.compiler.expressions.MacroSymbol
import io.github.subjekt.compiler.expressions.ParameterSymbol
import io.github.subjekt.compiler.expressions.QualifiedMacroSymbol
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExpressionSymbolResolveSpec : StringSpec({
    "An Expression with a single variable should return a single ParameterSymbol" {
        val expr = Expression("variable")
        val result = expr.symbols

        result shouldBe setOf(ParameterSymbol("variable"))
    }

    "An Expression with a plus operation should return two ParameterSymbols" {
        val expr = Expression("a + b")
        val result = expr.symbols

        result shouldBe setOf(ParameterSymbol("a"), ParameterSymbol("b"))
    }

    "An Expression with a literal should return an empty set of symbols" {
        val expr = Expression("\"literal\"")
        val result = expr.symbols
        result shouldBe emptySet()
    }

    "An Expression with a call accepting a Parameter argument should return a MacroSymbol and a ParameterSymbol" {
        val expr = Expression("macro(arg)")
        val result = expr.symbols
        result shouldBe setOf(ParameterSymbol("arg"), MacroSymbol("macro", 1))
    }

    "An Expression with a qualified call accepting a Parameter argument should return a MacroSymbol and a " +
        "ParameterSymbol" {
            val expr = Expression("module.macro(arg)")
            val result = expr.symbols
            result shouldBe setOf(QualifiedMacroSymbol("module", "macro", 1), ParameterSymbol("arg"))
        }
})
