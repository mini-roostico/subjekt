package io.github.subjekt.compiler.expressions.visitors.ir.impl.resolve

import io.github.subjekt.compiler.expressions.TypeException
import io.github.subjekt.compiler.expressions.ir.BinaryOperator
import io.github.subjekt.compiler.expressions.ir.IrBinaryOperation
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrCast
import io.github.subjekt.compiler.expressions.ir.IrCompleteSlice
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrEndOfSlice
import io.github.subjekt.compiler.expressions.ir.IrEndSlice
import io.github.subjekt.compiler.expressions.ir.IrFloatLiteral
import io.github.subjekt.compiler.expressions.ir.IrIntegerLiteral
import io.github.subjekt.compiler.expressions.ir.IrNativeType
import io.github.subjekt.compiler.expressions.ir.IrParameter
import io.github.subjekt.compiler.expressions.ir.IrSingleSlice
import io.github.subjekt.compiler.expressions.ir.IrStartEndSlice
import io.github.subjekt.compiler.expressions.ir.IrStartSlice
import io.github.subjekt.compiler.expressions.ir.IrStringLiteral
import io.github.subjekt.compiler.expressions.ir.Type
import io.github.subjekt.compiler.expressions.ir.utils.IrUtils
import io.github.subjekt.compiler.expressions.toParameterSymbol
import io.github.subjekt.compiler.expressions.visitors.ir.IrVisitor
import io.github.subjekt.core.definition.Context

class TypeVisitor(
    val context: Context,
) : IrVisitor<Type> {
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
        visit(node.value)
        tryInferType = previousTryInferType
        return type
    }

    override fun visitEndOfSlice(node: IrEndOfSlice): Type = Type.INTEGER.also { node.type = it }

    override fun visitCall(node: IrCall): Type =
        (tryInferType ?: Type.STRING).also {
            node.arguments.forEach { arg -> visit(arg) }
            node.type = it
        }

    override fun visitParameter(node: IrParameter): Type =
        node
            .toParameterSymbol()
            .resolveDefinedParameter(context)
            .value
            .run {
                IrUtils.tryInferType(
                    this,
                    tryInferType,
                    "Expected type $tryInferType for parameter '${node.identifier}",
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

    override fun visitCompleteSlice(node: IrCompleteSlice): Type {
        val previousTryInferType = tryInferType
        tryInferType = Type.INTEGER
        visit(node.start)
        visit(node.end)
        visit(node.step)
        tryInferType = previousTryInferType
        return (tryInferType ?: Type.STRING).also { node.type = it }
    }

    override fun visitEndSlice(node: IrEndSlice): Type {
        val previousTryInferType = tryInferType
        tryInferType = Type.INTEGER
        visit(node.end)
        tryInferType = previousTryInferType
        return (tryInferType ?: Type.STRING).also { node.type = it }
    }

    override fun visitStartEndSlice(node: IrStartEndSlice): Type {
        val previousTryInferType = tryInferType
        tryInferType = Type.INTEGER
        visit(node.start)
        visit(node.end)
        tryInferType = previousTryInferType
        return (tryInferType ?: Type.STRING).also { node.type = it }
    }

    override fun visitStartSlice(node: IrStartSlice): Type {
        val previousTryInferType = tryInferType
        tryInferType = Type.INTEGER
        visit(node.start)
        tryInferType = previousTryInferType
        return (tryInferType ?: Type.STRING).also { node.type = it }
    }
}
