package io.github.subjekt.nodes.expression

import io.github.subjekt.visitors.ExpressionIrVisitor

sealed class Node: Visitable {

  override fun <T> accept(irVisitor: ExpressionIrVisitor<T>) {
    irVisitor.visit(this)
  }

  class Id(val identifier: String) : Node()
  class Literal(val value: String) : Node()
  class Plus(val left: Node, val right: Node) : Node()
  class Call(val identifier: String, val arguments: List<Node>) : Node()
}
