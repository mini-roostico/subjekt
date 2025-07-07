/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions.visitors

import io.github.subjekt.engine.expressions.Expression
import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrParameter
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrTree
import io.kotest.common.KotestInternal
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@OptIn(KotestInternal::class)
class ExpressionIrCreationSpec : StringSpec({
    "visitVariable should return correct IrParameter" {
        val expr = Expression("variable")
        val result = expr.parseToIr()

        result shouldBe IrTree(IrParameter("variable", 1))
    }

    "visitPlusExpr should return correct IrExpressionPlus" {
        val expr = Expression("a + b")
        val result = expr.parseToIr()

        result shouldBe
            IrTree(
                IrBinaryOperation(
                    IrParameter("a", 1),
                    IrParameter("b", 1),
                    BinaryOperator.PLUS,
                    1,
                ),
            )
    }

    "visitLiteral should return correct IrLiteral" {
        val expr = Expression("\"literal\"")
        val result = expr.parseToIr()
        result shouldBe IrTree(IrStringLiteral("literal", 1))
    }

    "visitMacroCall should return correct IrCall" {
        val expr = Expression("macro(arg)")
        val result = expr.parseToIr()
        result shouldBe IrTree(IrCall("macro", listOf(IrParameter("arg", 1)), 1))
    }

    "visitDotCall should return correct IrDotCall".config(enabled = false) {
        val expr = Expression("module.macro(arg)")
        val result = expr.parseToIr()
        result shouldBe
            IrTree(
                IrDotCall(
                    IrParameter("module", 1),
                    "macro",
                    listOf(IrParameter("arg", 1)),
                    1,
                ),
            )
    }
})
