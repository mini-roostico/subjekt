package io.github.subjekt.engine.expressions.ir.utils

import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.engine.expressions.CallableSymbol
import io.github.subjekt.engine.expressions.SymbolNotFoundException
import io.github.subjekt.engine.expressions.TypeException
import io.github.subjekt.engine.expressions.ir.BinaryOperator
import io.github.subjekt.engine.expressions.ir.IrBasicNode
import io.github.subjekt.engine.expressions.ir.IrBinaryOperation
import io.github.subjekt.engine.expressions.ir.IrCall
import io.github.subjekt.engine.expressions.ir.IrDotCall
import io.github.subjekt.engine.expressions.ir.IrNode
import io.github.subjekt.engine.expressions.ir.Type
import io.github.subjekt.engine.expressions.toCallSymbol
import io.github.subjekt.engine.expressions.toQualifiedCallSymbol
import org.antlr.v4.kotlinruntime.ParserRuleContext

object IrUtils {
    /**
     * Represents the type of binary operation.
     */
    internal enum class BinaryOperationType {
        INTEGER,
        STRING,
    }

    /**
     * Creates a binary operation [IrBinaryOperation] from the given [ParserRuleContext]s and [BinaryOperator].
     */
    internal fun ParserRuleContext?.binaryOperation(
        right: ParserRuleContext?,
        op: BinaryOperator,
        line: Int = -1,
        contextualVisitError: (String) -> Nothing,
        visitMethod: (ParserRuleContext) -> IrNode?,
    ): IrBasicNode {
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

    /**
     * Calls the [CallableSymbol] with the given [arguments], resolving it to either a macro or a function.
     *
     * @throws SymbolNotFoundException if the symbol is not found in the context.
     */
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

    /**
     * Resolves the [IrCall] to a string result using the given [Context] and [visitMethod] to visit inner nodes.
     */
    fun IrCall.resolveCall(
        context: Context,
        visitMethod: (IrNode) -> String,
    ): String {
        val symbol = toCallSymbol()
        return context.callSymbol(symbol, arguments.map { visitMethod(it) })
    }

    /**
     * Resolves the [IrDotCall] to a string result using the given [Context] and [visitMethod] to visit inner nodes.
     */
    internal fun IrDotCall.resolveCall(
        context: Context,
        visitMethod: (IrNode) -> String,
    ): String {
        val symbol = toQualifiedCallSymbol()
        return context.callSymbol(symbol, arguments.map { visitMethod(it) })
    }

    /**
     * Infers the type of the given [value] based on the provided [type].
     *
     * @throws TypeException if the value cannot be converted to the specified type.
     */
    internal fun tryInferType(
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
