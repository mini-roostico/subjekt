package io.github.subjekt.engine.expressions

import io.github.subjekt.TestingUtility.shouldResolveTo
import io.github.subjekt.TestingUtility.shouldResolveToDouble
import io.github.subjekt.core.definition.Context
import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.visitors.ir.impl.ExpressionVisitor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BinaryOperationSpec : FunSpec({

    test("Integer operations") {
        "2 + 3" shouldResolveTo "5"
        "2 - 3" shouldResolveTo "-1"
        "2 * 3" shouldResolveTo "6"
        "7 / 2" shouldResolveTo "3.5"
        "7 % 3" shouldResolveTo "1"
        "'2' + 3" shouldResolveTo "5"
        "2 - '3'" shouldResolveTo "-1"
        "'2' * 3" shouldResolveTo "6"
        "7 / '2'" shouldResolveTo "3.5"
        "'7' % 3" shouldResolveTo "1"
    }

    test("Float operations") {
        "2.0 + 3.1" shouldResolveToDouble 5.1
        "2.0 - 3.0" shouldResolveToDouble -1.0
        "2.0 * 3.0" shouldResolveToDouble 6.0
        "7.0 / 3.0" shouldResolveToDouble 2.3333333333333335
        "7.0 % 3.0" shouldResolveToDouble 1.0
        "2.0 + '3.0'" shouldResolveToDouble 5.0
        "'2.1' - 3.0" shouldResolveToDouble -0.9
        "2.0 * '3.0'" shouldResolveToDouble 6.0
        "'7.0' / 3.0" shouldResolveToDouble 2.3333333333333335
    }

    test("Number operations") {
        "2 + 3.2" shouldResolveTo "5.2"
        "2.1 - 3" shouldResolveToDouble -0.9
        "2 * 3.1" shouldResolveTo "6.2"
        "7 / 2" shouldResolveTo "3.5"
        "7 % 3.0" shouldResolveToDouble 1.0
    }

    test("String concatenation") {
        "'a' .. 'b'" shouldResolveTo "ab"
        "'a' .. 1" shouldResolveTo "a1"
        "1 .. 'b'" shouldResolveTo "1b"
        "'a' .. '2.0'" shouldResolveTo "a2.0"
        "'2.0' .. 'b'" shouldResolveTo "2.0b"
    }

    test("Concat operator on numbers") {
        "1 .. 2" shouldResolveTo "12"
        "1.1 .. 2.2" shouldResolveTo "1.12.2"
        "1 .. 2.1" shouldResolveTo "12.1"
        "1.1 .. 2" shouldResolveTo "1.12"
    }

    test("Operator precedence") {
        "1 + 2 * 3" shouldResolveTo "7"
        "1 + 2 - 3" shouldResolveTo "0"
        "1 * 2 + 3" shouldResolveTo "5"
        "1 / 2 + 3" shouldResolveTo "3.5"
        "1 % 2 + 3" shouldResolveTo "4"
        "(1 + 2) * 3" shouldResolveTo "9"
        "(1 + 2) / 3" shouldResolveToDouble 1.0
    }

    test("Undefined type throws") {
        val ex =
            shouldThrow<UnsupportedOperationException> {
                ExpressionUtils.resolveBinaryOperation(
                    IrStringLiteral("1", 1),
                    IrStringLiteral("2", 2),
                    BinaryOperator.MINUS,
                ) { ExpressionVisitor(Context.empty).visit(it) }
            }
        ex.message shouldBe "Cannot subtract '2' from '1'."
    }

    test("Unsupported string operation throws") {
        val ex =
            shouldThrow<UnsupportedOperationException> {
                ExpressionUtils.resolveBinaryOperation(
                    IrStringLiteral("a", 1),
                    IrStringLiteral("b", 2),
                    BinaryOperator.MINUS,
                ) { ExpressionVisitor(Context.empty).visit(it) }
            }
        ex.message shouldBe "Cannot subtract 'b' from 'a'."
    }

    test("Invalid number throws") {
        val ex =
            shouldThrow<UnsupportedOperationException> {
                ExpressionUtils.resolveBinaryOperation(
                    IrStringLiteral("a", 1),
                    IrIntegerLiteral(3, 2),
                    BinaryOperator.MULTIPLY,
                ) { ExpressionVisitor(Context.empty).visit(it) }
            }
        ex.message shouldBe "Cannot multiply 'a' by int(3)."
    }

    test("Mixed operations") {
        "1 + '2' * 3" shouldResolveTo "7"
        "'1' + 2 - 3" shouldResolveTo "0"
        "2 * '3' + 4" shouldResolveTo "10"
        "'7' / 2 + 3" shouldResolveTo "6.5"
        "'7' % 3 + 2" shouldResolveTo "3"
        "1 .. 3 * 2" shouldResolveTo "16"
        "(1 .. '3') + ('2' .. 4) * (5 + 6)" shouldResolveTo "277"
    }
})
