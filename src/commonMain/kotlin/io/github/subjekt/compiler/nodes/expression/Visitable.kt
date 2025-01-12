/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.nodes.expression

import io.github.subjekt.compiler.visitors.ExpressionIrVisitor

/**
 * Represents a visitable node in the expression tree.
 */
interface Visitable {
    /**
     * Accepts the given visitor. This method is used to traverse the expression tree. [T] is the return type of the visitor,
     * and therefore is return by each visiting method.
     */
    fun <T> accept(irVisitor: ExpressionIrVisitor<T>)
}
