package io.github.subjekt.visitors

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.expression.Node
import io.github.subjekt.utils.MessageCollector
import io.github.subjekt.utils.Permutations.permute

class ExpressionResolveVisitor(
  var context: Context,
  val messageCollector: MessageCollector,
) : ExpressionIrVisitor<List<String>> {

  private fun Node.createError(message: String) {
    messageCollector.error("Line $line: $message")
  }

  override fun visitCall(node: Node.Call): List<String> {
    val macro = context.lookupMacro(node.identifier)
    if (macro == null) {
      node.createError("Macro '${node.identifier}' is not defined")
      return emptyList()
    }
    if (macro.argumentsNumber != node.arguments.size) {
      node.createError("Macro '${node.identifier}' expects ${macro.argumentsNumber} arguments, but got ${node.arguments.size}")
      return emptyList()
    }
    return node.arguments.map { arg -> visit(arg) }.permute().fold(emptyList<String>()) { acc, argsConfiguration ->
      if (argsConfiguration.toList().size != macro.argumentsNumber) {
        node.createError("Internal error while evaluating macro '${node.identifier}'")
        return emptyList()
      }
      val previousContext = context
      argsConfiguration.forEachIndexed { i, arg -> context.putParameter(macro.argumentsIdentifiers[i], arg) }
      val result = acc + macro.bodies.map { body -> body.resolveOne(context, messageCollector) }
      context = previousContext
      result
    }.toSet().toList()
  }

  override fun visitId(node: Node.Id): List<String> {
    val par = context.lookupParameter(node.identifier)
    if (par == null) {
      node.createError("Identifier '${node.identifier}' is not defined")
      return emptyList()
    }
    return listOf(par.toString())
  }

  override fun visitPlus(node: Node.Plus): List<String> {
    if (node.left is Node.Literal && node.right is Node.Literal) {
      return listOf(visit(node.left).first() + visit(node.right).first())
    }
    if (node.left is Node.Literal) {
      return visit(node.right).map { right -> visit(node.left).first() + right }
    }
    if (node.right is Node.Literal) {
      return visit(node.left).map { left -> left + visit(node.right).first() }
    }
    return visit(node.left).flatMap { left ->
      visit(node.right).map { right -> left + right }
    }
  }

  override fun visitLiteral(node: Node.Literal): List<String> =
    listOf(node.value)
}
