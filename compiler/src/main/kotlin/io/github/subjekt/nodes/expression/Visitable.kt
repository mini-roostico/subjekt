package io.github.subjekt.nodes.expression

import io.github.subjekt.visitors.ExpressionIrVisitor

interface Visitable {
  fun <T> accept(irVisitor: ExpressionIrVisitor<T>)
}
