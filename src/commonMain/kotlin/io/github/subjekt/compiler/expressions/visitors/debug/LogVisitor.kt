package io.github.subjekt.compiler.expressions.visitors.debug

import io.github.subjekt.compiler.expressions.ir.IrBinaryOperation
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrCast
import io.github.subjekt.compiler.expressions.ir.IrCompleteSlice
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrEndOfSlice
import io.github.subjekt.compiler.expressions.ir.IrEndSlice
import io.github.subjekt.compiler.expressions.ir.IrFloatLiteral
import io.github.subjekt.compiler.expressions.ir.IrIntegerLiteral
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.expressions.ir.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrSingleSlice
import io.github.subjekt.compiler.expressions.ir.IrStartEndSlice
import io.github.subjekt.compiler.expressions.ir.IrStartSlice
import io.github.subjekt.compiler.expressions.ir.IrStringLiteral
import io.github.subjekt.compiler.expressions.visitors.ir.impl.base.BaseExpressionVisitor

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

    override fun visitParameter(node: IrParameter) {
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

    override fun visitCompleteSlice(node: IrCompleteSlice) {
        indent("CompleteSlice: ${node.identifier}") {
            super.visitCompleteSlice(node)
        }
    }

    override fun visitEndSlice(node: IrEndSlice) {
        indent("EndSlice: ${node.identifier}") {
            super.visitEndSlice(node)
        }
    }

    override fun visitStartEndSlice(node: IrStartEndSlice) {
        indent("StartEndSlice: ${node.identifier}") {
            super.visitStartEndSlice(node)
        }
    }

    override fun visitStartSlice(node: IrStartSlice) {
        indent("StartSlice: ${node.identifier}") {
            super.visitStartSlice(node)
        }
    }
}
