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
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExpressionIrCreationSpec : StringSpec({
    "visitVariable should return correct IrParameter" {
        val expr = Expression("variable")
        val result = expr.parseToIr()

        result shouldBe IrNode.IrParameter("variable", 1)
    }

    "visitPlusExpr should return correct IrExpressionPlus" {
        val expr = Expression("a + b")
        val result = expr.parseToIr()

        result shouldBe IrNode.IrExpressionPlus(IrNode.IrParameter("a", 1), IrNode.IrParameter("b", 1), 1)
    }

    "visitLiteral should return correct IrLiteral" {
        val expr = Expression("\"literal\"")
        val result = expr.parseToIr()
        result shouldBe IrNode.IrLiteral("literal", 1)
    }

    "visitMacroCall should return correct IrCall" {
        val expr = Expression("macro(arg)")
        val result = expr.parseToIr()
        result shouldBe IrNode.IrCall("macro", listOf(IrNode.IrParameter("arg", 1)), 1)
    }

    "visitDotCall should return correct IrDotCall" {
        val expr = Expression("module.macro(arg)")
        val result = expr.parseToIr()
        result shouldBe IrNode.IrDotCall("module", "macro", listOf(IrNode.IrParameter("arg", 1)), 1)
    }
})
