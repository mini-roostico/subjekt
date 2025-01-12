package io.github.subjekt.compiler.visitors

import io.github.subjekt.compiler.nodes.expression.Node

/**
 * Visitor for the intermediate representation of an expression (a tree of [Node]s).
 */
interface ExpressionIrVisitor<T> {
  /**
   * Visits a [Node.Call]
   */
  fun visitCall(node: Node.Call): T

  /**
   * Visits a [Node.Id]
   */
  fun visitId(node: Node.Id): T

  /**
   * Visits a [Node.Plus]
   */
  fun visitPlus(node: Node.Plus): T

  /**
   * Visits a [Node.Literal]
   */
  fun visitLiteral(node: Node.Literal): T

  /**
   * Visits a [Node.DotCall]
   */
  fun visitDotCall(node: Node.DotCall): T

  /**
   * Visits a [Node], calling the appropriate visit method based on the type of the node.
   */
  fun visit(node: Node) =
    when (node) {
      is Node.Call -> visitCall(node)
      is Node.Id -> visitId(node)
      is Node.Plus -> visitPlus(node)
      is Node.Literal -> visitLiteral(node)
      is Node.DotCall -> visitDotCall(node)
    }
}
