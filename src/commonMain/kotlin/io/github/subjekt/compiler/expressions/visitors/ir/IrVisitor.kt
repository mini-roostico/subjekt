package io.github.subjekt.compiler.expressions.visitors.ir

import io.github.subjekt.compiler.expressions.ir.Error
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
import io.github.subjekt.compiler.expressions.ir.IrTree
import io.github.subjekt.utils.Utils

/**
 * Visitor for the intermediate representation of an expression (a tree of
 * [io.github.subjekt.compiler.expressions.ir.IrNode]s).
 */
interface IrVisitor<T> {
    fun visitBinaryOperation(node: IrBinaryOperation): T

    fun visitCast(node: IrCast): T

    fun visitEndOfSlice(node: IrEndOfSlice): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrCall] node.
     */
    fun visitCall(node: IrCall): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrParameter] node.
     */
    fun visitParameter(node: IrParameter): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrDotCall] node.
     */
    fun visitDotCall(node: IrDotCall): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrSingleSlice] node.
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
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrCompleteSlice] node.
     */
    fun visitCompleteSlice(node: IrCompleteSlice): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrEndSlice] node.
     */
    fun visitEndSlice(node: IrEndSlice): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrStartEndSlice] node.
     */
    fun visitStartEndSlice(node: IrStartEndSlice): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrStartSlice] node.
     */
    fun visitStartSlice(node: IrStartSlice): T

    /**
     * Visits a [io.github.subjekt.compiler.expressions.ir.IrNode], calling the appropriate visit method based on the
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
            is IrCompleteSlice -> visitCompleteSlice(node)
            is IrEndSlice -> visitEndSlice(node)
            is IrStartEndSlice -> visitStartEndSlice(node)
            is IrStartSlice -> visitStartSlice(node)
            is IrParameter -> visitParameter(node)
            is IrTree -> visit(node.node)
            is Error -> Utils.parsingFail { "Cannot visit an error node: $node" }
        }
}
