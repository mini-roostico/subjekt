/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions

import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.definition.Context
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExpressionResolvingSpec : StringSpec({

    "Resolving a simple expression should work" {
        val expression = Expression("'1' .. '1'")
        val result = expression.resolve(Context.empty)
        result shouldBe "11"
    }

    "Resolving a simple expression with a parameter should work" {
        val expression = Expression("param .. '1'")
        val result = expression.resolve(Context().withParameter("param", "2"))
        result shouldBe "21"
    }

    "Resolving a simple expression with a macro should work" {
        val expression = Expression("macro('1') .. '1'")
        val result = expression.resolve(Context().withMacro("macro", listOf("arg"), Resolvable("\${{ arg }}")))
        result shouldBe "11"
    }

    "Resolving a simple expression with a function should work" {
        val expression = Expression("fun('1') .. '1'")
        val result = expression.resolve(Context().withFunction("fun") { args -> (args.first().toInt() + 1).toString() })
        result shouldBe "21"
    }

    "Resolving an expression with multiple parameters and calls should work" {
        val expression = Expression("macro(param1, fun(param1, param2)) .. '1' .. fun('6', '5')")
        val result =
            expression.resolve(
                Context()
                    .withParameter("param1", "2")
                    .withParameter("param2", "3")
                    .withMacro("macro", listOf("arg1", "arg2"), Resolvable("\${{ arg1 }},\${{ ' ' .. arg2 }}"))
                    .withFunction("fun") {
                        require(it.size == 2)
                        (it.first().toInt() + it.last().toInt()).toString()
                    },
            )
        result shouldBe "2, 5111"
    }

    "Resolving an expression with nested function calls should work" {
        val expression = Expression("fun(fun('1'))")
        val result =
            expression.resolve(
                Context().withFunction("fun") { args -> (args.first().toInt() + 1).toString() },
            )
        result shouldBe "3"
    }

    "Resolving an expression with nested macro calls should work" {
        val expression = Expression("macro(macro('1'))")
        val result =
            expression.resolve(
                Context().withMacro("macro", listOf("arg"), Resolvable("\${{ arg }} + 1")),
            )
        result shouldBe "1 + 1 + 1"
    }

    "Resolving an expression with a missing function should throw an exception" {
        val expression = Expression("fun('1')")
        val exception =
            shouldThrow<SymbolNotFoundException> {
                expression.resolve(Context.empty)
            }
        exception.message shouldBe "Called 'fun' symbol with 1 arguments, but 'fun/1' cannot be resolved."
    }

    "Resolving an expression with a unresolved parameter should throw an exception" {
        val expression = Expression("param + '1'")
        val exception =
            shouldThrow<SymbolNotFoundException> {
                expression.resolve(Context.empty)
            }
        exception.message shouldBe "Parameter 'param' cannot be resolved."
    }

    "Resolving an expression with a unresolved macro should throw an exception" {
        val expression = Expression("macro('1') .. '1'")
        val exception =
            shouldThrow<SymbolNotFoundException> {
                expression.resolve(Context.empty)
            }
        exception.message shouldBe "Called 'macro' symbol with 1 arguments, but 'macro/1' cannot be resolved."
    }

    "Resolving an expression with a macro using a missing parameter in its body should throw an exception" {
        val expression = Expression("macro('1')")
        val exception =
            shouldThrow<SymbolNotFoundException> {
                expression.resolve(
                    Context().withMacro("macro", listOf("arg"), Resolvable("\${{ arg }} + \${{ missing }}")),
                )
            }
        exception.message shouldBe "Parameter 'missing' cannot be resolved."
    }
})
