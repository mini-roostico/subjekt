package io.github.subjekt.visitors

import io.github.subjekt.nodes.Context
import io.github.subjekt.nodes.expression.Node
import io.github.subjekt.utils.MessageCollector

/**
 * Visitor used to resolve an expression to a list of possible string values. This is used to evaluate the expressions
 * inside a [io.github.subjekt.nodes.suite.Template].
 */
class ExpressionResolveVisitor(
  /**
   * Starting context for the expression.
   */
  var context: Context,
  /**
   * Message collector to report errors.
   */
  val messageCollector: MessageCollector,
) : ExpressionIrVisitor<String> {

  private fun Node.createError(message: String) {
    messageCollector.error(message, context, this.line)
  }

  override fun visitCall(node: Node.Call): String {
    val macro = context.lookupDefinedMacro(node.identifier)
    if (macro == null) {
      // if the macro is not found in the current context, we try to find it in the standard module
      val macro = context.lookupModule("std", node.identifier)
      if (macro == null) {
        node.createError("Macro '${node.identifier}' is not defined")
        return ""
      }
      return visit(Node.DotCall("std", node.identifier, node.arguments, node.line))
    }
    if (macro.argumentsIdentifiers.size != node.arguments.size) {
      node.createError(
        "Macro '${node.identifier}' expects ${macro.argumentsIdentifiers.size} arguments, " +
          "but got ${node.arguments.size}",
      )
      return ""
    }
    val previousContext = context
    node.arguments.map { arg -> visit(arg) }
      .forEachIndexed { i, arg -> context.putParameter(macro.argumentsIdentifiers[i], arg) }
    val result = macro.body.resolve(context, messageCollector)
    context = previousContext
    return result
  }

  override fun visitDotCall(node: Node.DotCall): String {
    val customMacro = context.lookupModule(node.moduleId, node.callId)
    if (customMacro == null) {
      node.createError("Macro '${node.callId}' is not defined in module '${node.moduleId}'")
      return ""
    }
    if (customMacro.numberOfArguments != -1 && customMacro.numberOfArguments != node.arguments.size) {
      node.createError("Macro '${node.callId}' expects ${customMacro.numberOfArguments} arguments, but got ${node.arguments.size}")
      return ""
    }
    return customMacro.eval(node.arguments.map { arg -> visit(arg) }.toList(), messageCollector)
  }

  override fun visitId(node: Node.Id): String {
    val par = context.lookupParameter(node.identifier)
    if (par == null) {
      node.createError("Identifier '${node.identifier}' is not defined")
      return ""
    }
    return par.toString()
  }

  override fun visitPlus(node: Node.Plus): String =
    visit(node.left) + visit(node.right)

  override fun visitLiteral(node: Node.Literal): String = node.value
}
