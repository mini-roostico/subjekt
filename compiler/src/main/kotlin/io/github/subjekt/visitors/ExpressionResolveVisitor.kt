package io.github.subjekt.visitors

import io.github.subjekt.nodes.expression.Node

class ExpressionResolveVisitor: ExpressionIrVisitor<List<String>> {
  override fun visitCall(node: Node.Call): List<String> {
    TODO("Not yet implemented")
  }

  override fun visitId(node: Node.Id): List<String> {
    TODO("Not yet implemented")
  }

  override fun visitPlus(node: Node.Plus): List<String> {
    TODO("Not yet implemented")
  }

  override fun visitLiteral(node: Node.Literal): List<String> {
    TODO("Not yet implemented")
  }
}
