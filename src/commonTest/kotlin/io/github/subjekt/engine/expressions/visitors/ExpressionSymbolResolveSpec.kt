/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions.visitors

import io.github.subjekt.engine.expressions.CallSymbol
import io.github.subjekt.engine.expressions.Expression
import io.github.subjekt.engine.expressions.ParameterSymbol
import io.github.subjekt.engine.expressions.QualifiedCallSymbol
import io.github.subjekt.engine.expressions.ir.IrParameter
import io.kotest.common.KotestInternal
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@OptIn(KotestInternal::class)
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
        result shouldBe setOf(ParameterSymbol("arg"), CallSymbol("macro", 1))
    }

    "An Expression with a qualified call accepting a Parameter argument should return a MacroSymbol and a " +
        "ParameterSymbol".config(enabled = false) {
            val expr = Expression("module.macro(arg)")
            val result = expr.symbols
            result shouldBe
                setOf(
                    QualifiedCallSymbol(IrParameter("module", 1), "macro", 1),
                    ParameterSymbol("arg"),
                )
        }
})
