package io.github.subjekt.engine.expressions.visitors.ir.impl

import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.value.Type
import io.github.subjekt.engine.expressions.TypeException
import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrCast
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrEndOfSlice
import io.github.subjekt.engine.expressions.ir.IrFloatLiteral
import io.github.subjekt.engine.expressions.ir.IrIdentifier
import io.github.subjekt.engine.expressions.ir.IrIntegerLiteral
import io.github.subjekt.engine.expressions.ir.IrNativeType
import io.github.subjekt.engine.expressions.ir.IrRangeSlice
import io.github.subjekt.engine.expressions.ir.IrSingleSlice
import io.github.subjekt.engine.expressions.ir.IrStringLiteral
import io.github.subjekt.engine.expressions.ir.IrUnaryOperation
import io.github.subjekt.engine.expressions.ir.utils.IrUtils
import io.github.subjekt.engine.expressions.toParameterSymbol
import io.github.subjekt.engine.expressions.visitors.ir.IrVisitor

/**
 * A visitor that infers the type of an expression in the intermediate representation (IR).
 * It traverses the IR tree and assigns types to nodes based on the operations and operands.
 *
 * @property context The context in which the IR is evaluated, providing access to symbols and definitions.
 */
class TypeVisitor(
    val context: Context,
) : IrVisitor<Type> {
    /**
     * A temporary variable to hold the type we are trying to infer.
     * This is used to handle cases where the type is not explicitly defined,
     * such as in binary operations or function calls.
     */
    private var tryInferType: Type? = null

    override fun visitBinaryOperation(node: IrBinaryOperation): Type {
        val leftType = visit(node.left)
        val rightType = visit(node.right)
        if (leftType == Type.UNDEFINED || rightType == Type.UNDEFINED) {
            throw TypeException(
                "Cannot infer type for binary operation '${node.operator}' " +
                    "with left type '$leftType' and right type '$rightType'.",
            )
        }
        if (node.operator == BinaryOperator.CONCAT) return Type.STRING.also { node.type = it }
        if ((node.operator == BinaryOperator.DIVIDE) || leftType == Type.FLOAT || rightType == Type.FLOAT) {
            return Type.FLOAT.also { node.type = it }
        }
        // we only have integers and strings left
        if (leftType == Type.INTEGER || rightType == Type.INTEGER) {
            return Type.INTEGER.also { node.type = it }
        }
        // now we have only strings left, both are strings
        if (node.operator == BinaryOperator.PLUS && tryInferType == Type.STRING) {
            // we are using plus operator, and we are trying to infer a string type, so this must be a string
            // concatenation
            return Type.STRING.also { node.type = it }
        }
        // couldn't settle on float or integer, so we use the generic number type
        return Type.NUMBER.also { node.type = it }
    }

    override fun visitCast(node: IrCast): Type {
        val previousTryInferType = tryInferType
        val type =
            when (node.targetType) {
                IrNativeType.INTEGER -> Type.INTEGER
                IrNativeType.FLOAT -> Type.FLOAT
                IrNativeType.STRING -> Type.STRING
            }.also { node.type = it }
        tryInferType = type
        node.value?.accept(this)
        tryInferType = previousTryInferType
        return type
    }

    override fun visitEndOfSlice(node: IrEndOfSlice): Type = Type.INTEGER.also { node.type = it }

    override fun visitCall(node: IrCall): Type =
        (tryInferType ?: Type.STRING).also {
            node.arguments.forEach { arg -> visit(arg) }
            node.type = it
        }

    override fun visitParameter(node: IrIdentifier): Type =
        node
            .toParameterSymbol()
            .resolveDefinedParameter(context)
            .value
            .run {
                IrUtils.tryInferType(
                    this,
                )
            }.also { node.type = it }

    override fun visitDotCall(node: IrDotCall): Type = (tryInferType ?: Type.STRING).also { node.type = it }

    override fun visitSingleSlice(node: IrSingleSlice): Type {
        val previousTryInferType = tryInferType
        tryInferType = Type.INTEGER
        visit(node.indexExpression)
        tryInferType = previousTryInferType
        return (tryInferType ?: Type.STRING).also { node.type = it }
    }

    override fun visitFloatLiteral(node: IrFloatLiteral): Type = Type.FLOAT.also { node.type = it }

    override fun visitIntegerLiteral(node: IrIntegerLiteral): Type = Type.INTEGER.also { node.type = it }

    override fun visitStringLiteral(node: IrStringLiteral): Type = Type.STRING.also { node.type = it }

    override fun visitRangeSlice(node: IrRangeSlice): Type {
        val previousTryInferType = tryInferType
        tryInferType = Type.INTEGER
        visit(node.start)
        visit(node.end)
        visit(node.step)
        tryInferType = previousTryInferType
        return (tryInferType ?: Type.STRING).also { node.type = it }
    }

    override fun visitUnaryOperation(node: IrUnaryOperation): Type {
        val type = visit(node.operand)
        if (type == Type.UNDEFINED) {
            throw TypeException(
                "Cannot infer type for unary operation '${node.operator}' with operand type '$type'.",
            )
        } else if (type == Type.STRING) {
            throw TypeException(
                "Cannot perform unary operation '${node.operator}' on a string type. " +
                    "Expected a numeric type.",
            )
        }
        return type
    }
}
