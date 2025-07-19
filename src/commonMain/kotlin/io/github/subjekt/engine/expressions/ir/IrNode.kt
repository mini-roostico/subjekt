/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.engine.expressions.ir

import io.github.subjekt.core.value.Type
import io.github.subjekt.engine.expressions.visitors.ir.IrVisitor

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

    /**
     * Accepts a [irVisitor] to visit this node.
     */
    fun <T> accept(irVisitor: IrVisitor<T>) = irVisitor.visit(this)
}

/**
 * IR wrapper for the entire tree.
 */
data class IrTree(
    /**
     * The root node of the IR tree.
     */
    val node: IrNode,
) : IrNode(-1)

/**
 * Represents an error node.
 */
data class Error(
    override val line: Int,
) : IrNode(line)

/**
 * Represents a simple node in the IR tree.
 */
sealed class IrBasicNode(
    /**
     * Line in the source code where this node is located.
     */
    override val line: Int,
) : IrNode(line)
