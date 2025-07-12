package io.github.subjekt.engine.expressions.visitors.debug

import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrEndOfSlice
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIdentifier
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.engine.expressions.visitors.ir.impl.BaseExpressionVisitor

/**
 * A visitor that logs the structure of the IR tree to the console.
 *
 * @property enabled Whether the logging is enabled. If false, no logging will occur.
 */
class LogVisitor(
    private val enabled: Boolean = true,
) : BaseExpressionVisitor<Unit>(Unit) {
    private var indent = ""

    override fun visit(node: IrNode) {
        if (enabled) super.visit(node)
    }

    private fun indent(
        log: String,
        unit: () -> Unit,
    ) {
        println(indent + log)
        val previousIndent = indent
        indent += "  "
        unit()
        indent = previousIndent
    }

    override fun visitBinaryOperation(node: IrBinaryOperation) {
        indent("BinaryOperation: ${node.operator}") {
            super.visitBinaryOperation(node)
        }
    }

    override fun visitCast(node: IrCast) {
        indent("Cast: ${node.type}") {
            super.visitCast(node)
        }
    }

    override fun visitEndOfSlice(node: IrEndOfSlice) {
        indent("EndOfSlice") {
            super.visitEndOfSlice(node)
        }
    }

    override fun visitCall(node: IrCall) {
        indent("Call: ${node.identifier}") {
            super.visitCall(node)
        }
    }

    override fun visitParameter(node: IrIdentifier) {
        indent("Parameter: ${node.identifier}") {
            super.visitParameter(node)
        }
    }

    override fun visitDotCall(node: IrDotCall) {
        indent("DotCall: ${node.identifier}") {
            super.visitDotCall(node)
        }
    }

    override fun visitSingleSlice(node: IrSingleSlice) {
        indent("SingleSlice: ${node.identifier}") {
            super.visitSingleSlice(node)
        }
    }

    override fun visitFloatLiteral(node: IrFloatLiteral) {
        indent("FloatLiteral: ${node.value}") {
            super.visitFloatLiteral(node)
        }
    }

    override fun visitIntegerLiteral(node: IrIntegerLiteral) {
        indent("IntegerLiteral: ${node.value}") {
            super.visitIntegerLiteral(node)
        }
    }

    override fun visitStringLiteral(node: IrStringLiteral) {
        indent("StringLiteral: ${node.value}") {
            super.visitStringLiteral(node)
        }
    }

    override fun visitRangeSlice(node: IrRangeSlice) {
        indent("RangeSlice: ${node.identifier}") {
            indent("Start: ${node.start}") {
                visit(node.start)
            }
            indent("End: ${node.end}") {
                visit(node.end)
            }
            indent("Step: ${node.step}") {
                visit(node.step)
            }
        }
    }

    override fun visitUnaryOperation(node: IrUnaryOperation) {
        indent("UnaryOperation: ${node.operator}") {
            super.visitUnaryOperation(node)
        }
    }
}
