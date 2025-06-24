/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.expressions.ir

import io.github.subjekt.compiler.expressions.visitors.ir.IrVisitor

/**
 * Represents a node in the expression tree.
 */
sealed class IrNode(
    /**
     * The line in the source code where this node is located.
     */
    open val line: Int,
) {
    var type: Type = Type.UNDEFINED

    fun <T> accept(irVisitor: IrVisitor<T>) = irVisitor.visit(this)
}

enum class Type {
    STRING,
    INTEGER,
    FLOAT,
    NUMBER,
    UNDEFINED,
}

/**
 * IR wrapper for the entire tree.
 */
data class IrTree(
    val node: IrNode,
) : IrNode(-1)

/**
 * Represents an error node.
 */
data class Error(
    override val line: Int,
) : IrNode(line)
