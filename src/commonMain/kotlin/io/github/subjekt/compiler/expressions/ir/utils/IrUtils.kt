package io.github.subjekt.compiler.expressions.ir.utils

import io.github.subjekt.compiler.expressions.CallableSymbol
import io.github.subjekt.compiler.expressions.SymbolNotFoundException
import io.github.subjekt.compiler.expressions.TypeException
import io.github.subjekt.compiler.expressions.ir.BinaryOperator
import io.github.subjekt.compiler.expressions.ir.IrAtomicNode
import io.github.subjekt.compiler.expressions.ir.IrBinaryOperation
import io.github.subjekt.compiler.expressions.ir.IrCall
import io.github.subjekt.compiler.expressions.ir.IrDotCall
import io.github.subjekt.compiler.expressions.ir.IrNode
import io.github.subjekt.compiler.expressions.ir.Type
import io.github.subjekt.compiler.expressions.toCallSymbol
import io.github.subjekt.compiler.expressions.toQualifiedCallSymbol
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import org.antlr.v4.kotlinruntime.ParserRuleContext

object IrUtils {
    internal enum class BinaryOperationType {
        INTEGER,
        STRING,
    }

    internal fun ParserRuleContext?.binaryOperation(
        right: ParserRuleContext?,
        op: BinaryOperator,
        line: Int = -1,
        contextualVisitError: (String) -> Nothing,
        visitMethod: (ParserRuleContext) -> IrNode?,
    ): IrAtomicNode {
        val left =
            this?.let { visitMethod(it) }
                ?: contextualVisitError("left")
        val right =
            right?.let { visitMethod(it) }
                ?: contextualVisitError("right")
        return IrBinaryOperation(
            left = left,
            right = right,
            operator = op,
            line = line,
        )
    }

    /**
     * Calls the [DefinedMacro] with the given [arguments], internally resolving the macro's
     * [io.github.subjekt.core.Resolvable].
     */
    fun DefinedMacro.call(
        context: Context,
        arguments: List<String>,
    ): String {
        argumentsIdentifiers
            .foldIndexed(context) { index, acc, id ->
                acc.withParameter(id, arguments[index])
            }.let { newContext ->
                return value.resolve(newContext)
            }
    }

    fun Context.callSymbol(
        symbol: CallableSymbol,
        arguments: List<String>,
    ): String {
        val definedMacro = symbol.resolveDefinedMacro(this)
        return if (definedMacro != null) {
            definedMacro.call(this, arguments)
        } else {
            val definedFunction = symbol.resolveFunction(this)
            if (definedFunction == null) {
                throw SymbolNotFoundException(symbol)
            }
            definedFunction(arguments)
        }
    }

    fun IrCall.resolveCall(
        context: Context,
        visitMethod: (IrNode) -> String,
    ): String {
        val symbol = toCallSymbol()
        return context.callSymbol(symbol, arguments.map { visitMethod(it) })
    }

    fun IrDotCall.resolveCall(
        context: Context,
        visitMethod: (IrNode) -> String,
    ): String {
        val symbol = toQualifiedCallSymbol()
        return context.callSymbol(symbol, arguments.map { visitMethod(it) })
    }

    fun tryInferType(
        value: String,
        type: Type?,
        message: String,
    ): Type =
        when (type) {
            Type.INTEGER -> {
                value.toIntOrNull() ?: throw TypeException(message)
                Type.INTEGER
            }

            Type.FLOAT -> {
                value.toFloatOrNull() ?: throw TypeException(message)
                Type.FLOAT
            }

            Type.NUMBER -> {
                value.toFloatOrNull() ?: value.toIntOrNull() ?: throw TypeException(message)
                Type.NUMBER
            }

            else -> Type.STRING
        }
}
