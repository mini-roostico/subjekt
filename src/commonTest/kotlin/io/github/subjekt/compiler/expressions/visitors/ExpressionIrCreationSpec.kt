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
import io.github.subjekt.compiler.expressions.ir.IrNode.IrCall
import io.github.subjekt.compiler.expressions.ir.IrNode.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrNode.IrExpressionPlus
import io.github.subjekt.compiler.expressions.ir.IrNode.IrLiteral
import io.github.subjekt.compiler.expressions.ir.IrNode.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrNode.IrTree
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ExpressionIrCreationSpec : StringSpec({
    "visitVariable should return correct IrParameter" {
        val expr = Expression("variable")
        val result = expr.parseToIr()

        result shouldBe IrTree(IrParameter("variable", 1))
    }

    "visitPlusExpr should return correct IrExpressionPlus" {
        val expr = Expression("a + b")
        val result = expr.parseToIr()

        result shouldBe IrTree(IrExpressionPlus(IrParameter("a", 1), IrParameter("b", 1), 1))
    }

    "visitLiteral should return correct IrLiteral" {
        val expr = Expression("\"literal\"")
        val result = expr.parseToIr()
        result shouldBe IrTree(IrLiteral("literal", 1))
    }

    "visitMacroCall should return correct IrCall" {
        val expr = Expression("macro(arg)")
        val result = expr.parseToIr()
        result shouldBe IrTree(IrCall("macro", listOf(IrParameter("arg", 1)), 1))
    }

    "visitDotCall should return correct IrDotCall" {
        val expr = Expression("module.macro(arg)")
        val result = expr.parseToIr()
        result shouldBe IrTree(IrDotCall("module", "macro", listOf(IrParameter("arg", 1)), 1))
    }
})
