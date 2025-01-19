/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.ir

/**
 * Represents a node in the expression tree.
 */
sealed class IrNode(
    /**
     * The line in the source code where this node is located.
     */
    open val line: Int,
) {
//    fun <T> accept(irVisitor: ExpressionIrVisitor<T>) {
//        irVisitor.visit(this)
//    }

    /**
     * Represents an identifier node (e.g. `${{ name }}`).
     */
    data class IrParameter(
        /**
         * Identifier value.
         */
        val identifier: String,
        override val line: Int,
    ) : IrNode(line)

    /**
     * Represents a literal node (e.g. `${{ "Hello" }}` or `${{ 'Hello' }}`).
     */
    data class IrLiteral(
        /**
         * Literal value (without quotes).
         */
        val value: String,
        override val line: Int,
    ) : IrNode(line)

    /**
     * Represents a plus node (e.g. `${{ 1 + 1 }}`).
     */
    data class IrExpressionPlus(
        /**
         * Left operand.
         */
        val left: IrNode,
        /**
         * Right operand.
         */
        val right: IrNode,
        override val line: Int,
    ) : IrNode(line)

    /**
     * Represents a call node (e.g. `${{ name() }}`).
     */
    data class IrCall(
        /**
         * Identifier of the call.
         */
        val identifier: String,
        /**
         * Arguments expressions of the call.
         */
        val arguments: List<IrNode>,
        override val line: Int,
    ) : IrNode(line)

    /**
     * Represents a dot call node (e.g. `${{ std.name() }}`).
     */
    data class IrDotCall(
        /**
         * Identifier of the module (e.g. `std`).
         */
        val moduleId: String,
        /**
         * Identifier of the call (e.g. `name`).
         */
        val callId: String,
        /**
         * Arguments expressions of the call.
         */
        val arguments: List<IrNode>,
        override val line: Int,
    ) : IrNode(line)

    /**
     * Represents an error node.
     */
    data class Error(
        override val line: Int,
    ) : IrNode(line)
}
