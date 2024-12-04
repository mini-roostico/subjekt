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
    messageCollector.error(message, context, this.line)
  }

  override fun visitCall(node: Node.Call): List<String> {
    val macro = context.lookupMacro(node.identifier)
    if (macro == null) {
      // if the macro is not found in the current context, we try to find it in the standard module
      val macro = context.lookupModule("std", node.identifier)
      if (macro == null) {
        node.createError("Macro '${node.identifier}' is not defined")
        return emptyList()
      }
      return visit(Node.DotCall("std", node.identifier, node.arguments, node.line))
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
    }.toList()
  }

  override fun visitDotCall(node: Node.DotCall): List<String> {
    val customMacro = context.lookupModule(node.moduleId, node.callId)
    if (customMacro == null) {
      node.createError("Macro '${node.callId}' is not defined in module '${node.moduleId}'")
      return emptyList()
    }
    if (customMacro.numberOfArguments != -1 && customMacro.numberOfArguments != node.arguments.size) {
      node.createError("Macro '${node.callId}' expects ${customMacro.numberOfArguments} arguments, but got ${node.arguments.size}")
      return emptyList()
    }
    return node.arguments.map { arg -> visit(arg) }.permute().fold(emptyList<String>()) { acc, argsConfiguration ->
      if (customMacro.numberOfArguments != -1 && argsConfiguration.toList().size != customMacro.numberOfArguments) {
        node.createError("Internal error while evaluating macro '${node.callId}'")
        return emptyList()
      }
      val previousContext = context
      val result = customMacro.eval(argsConfiguration.toList(), messageCollector)
      context = previousContext
      result
    }.toList()
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

  override fun visitLiteral(node: Node.Literal): List<String> = listOf(node.value)
}
