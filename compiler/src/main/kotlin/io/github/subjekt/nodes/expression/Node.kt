package io.github.subjekt.nodes.expression

import io.github.subjekt.visitors.ExpressionIrVisitor

/**
 * Represents a node in the expression tree.
 */
sealed class Node(
  /**
   * The line in the source code where this node is located.
   */
  val line: Int,
) : Visitable {

  override fun <T> accept(irVisitor: ExpressionIrVisitor<T>) {
    irVisitor.visit(this)
  }

  /**
   * Represents an identifier node (e.g. `${{ name }}`).
   */
  class Id(
    /**
     * Identifier value.
     */
    val identifier: String,
    line: Int,
  ) : Node(line)

  /**
   * Represents a literal node (e.g. `${{ "Hello" }}` or `${{ 'Hello' }}`).
   */
  class Literal(
    /**
     * Literal value (without quotes).
     */
    val value: String,
    line: Int,
  ) : Node(line)

  /**
   * Represents a plus node (e.g. `${{ 1 + 1 }}`).
   */
  class Plus(
    /**
     * Left operand.
     */
    val left: Node,
    /**
     * Right operand.
     */
    val right: Node,
    line: Int,
  ) : Node(line)

  /**
   * Represents a call node (e.g. `${{ name() }}`).
   */
  class Call(
    /**
     * Identifier of the call.
     */
    val identifier: String,
    /**
     * Arguments expressions of the call.
     */
    val arguments: List<Node>,
    line: Int,
  ) : Node(line)

  /**
   * Represents a dot call node (e.g. `${{ std.name() }}`).
   */
  class DotCall(
    /**
     * Identifier of the module (e.g. `std`).
     */
    val moduleId: String,
    /**
     * Identifier of the call (e.g. `name`).
     */
    val callId: String,
    /**
     * Arguments expressions of the call.
     */
    val arguments: List<Node>,
    line: Int,
  ) : Node(line)
}
