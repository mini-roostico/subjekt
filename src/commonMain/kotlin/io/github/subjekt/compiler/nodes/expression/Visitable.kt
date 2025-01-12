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
