package io.github.subjekt.engine.expressions.visitors.ir

import io.github.subjekt.engine.expressions.ir.Error
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrEndOfSlice
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.IrParameter
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrTree
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.utils.Utils

/**
 * Visitor for the intermediate representation of an expression (a tree of
 * [io.github.subjekt.engine.expressions.ir.IrNode]s).
 */
interface IrVisitor<T> {
    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrBinaryOperation] node.
     */
    fun visitBinaryOperation(node: IrBinaryOperation): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrCast] node.
     */
    fun visitCast(node: IrCast): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrEndOfSlice] node.
     */
    fun visitEndOfSlice(node: IrEndOfSlice): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrCall] node.
     */
    fun visitCall(node: IrCall): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrParameter] node.
     */
    fun visitParameter(node: IrParameter): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrDotCall] node.
     */
    fun visitDotCall(node: IrDotCall): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrSingleSlice] node.
     */
    fun visitSingleSlice(node: IrSingleSlice): T

    /**
     * Visits a [IrFloatLiteral] node.
     */
    fun visitFloatLiteral(node: IrFloatLiteral): T

    /**
     * Visits a [IrIntegerLiteral] node.
     */
    fun visitIntegerLiteral(node: IrIntegerLiteral): T

    /**
     * Visits a [IrStringLiteral] node.
     */
    fun visitStringLiteral(node: IrStringLiteral): T

    /**
     * Visits a [IrRangeSlice] node.
     */
    fun visitRangeSlice(node: IrRangeSlice): T

    /**
     * Visits a [IrUnaryOperation] node.
     */
    fun visitUnaryOperation(node: IrUnaryOperation): T

    /**
     * Visits a [io.github.subjekt.engine.expressions.ir.IrNode], calling the appropriate visit method based on the
     * type of the node.
     */
    fun visit(node: IrNode): T =
        when (node) {
            is IrBinaryOperation -> visitBinaryOperation(node)
            is IrCast -> visitCast(node)
            is IrEndOfSlice -> visitEndOfSlice(node)
            is IrCall -> visitCall(node)
            is IrDotCall -> visitDotCall(node)
            is IrSingleSlice -> visitSingleSlice(node)
            is IrFloatLiteral -> visitFloatLiteral(node)
            is IrIntegerLiteral -> visitIntegerLiteral(node)
            is IrStringLiteral -> visitStringLiteral(node)
            is IrParameter -> visitParameter(node)
            is IrRangeSlice -> visitRangeSlice(node)
            is IrUnaryOperation -> visitUnaryOperation(node)
            is IrTree -> visit(node.node)
            is Error -> Utils.parsingFail { "Cannot visit an error node: $node" }
        }
}
