package io.github.subjekt.nodes.expression

import io.github.subjekt.visitors.ExpressionIrVisitor

sealed class Node(val line: Int) : Visitable {

  override fun <T> accept(irVisitor: ExpressionIrVisitor<T>) {
    irVisitor.visit(this)
  }

  class Id(val identifier: String, line: Int) : Node(line)
  class Literal(val value: String, line: Int) : Node(line)
  class Plus(val left: Node, val right: Node, line: Int) : Node(line)
  class Call(val identifier: String, val arguments: List<Node>, line: Int) : Node(line)
  class DotCall(val moduleId: String, val callId: String, val arguments: List<Node>, line: Int) : Node(line)
}
