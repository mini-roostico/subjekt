package io.github.subjekt.visitors

import io.github.subjekt.nodes.expression.Node

interface ExpressionIrVisitor<T> {
  fun visitCall(node: Node.Call): T
  fun visitId(node: Node.Id): T
  fun visitPlus(node: Node.Plus): T
  fun visitLiteral(node: Node.Literal): T

  fun visit(node: Node) =
    when (node) {
      is Node.Call -> visitCall(node)
      is Node.Id -> visitId(node)
      is Node.Plus -> visitPlus(node)
      is Node.Literal -> visitLiteral(node)
    }
}
