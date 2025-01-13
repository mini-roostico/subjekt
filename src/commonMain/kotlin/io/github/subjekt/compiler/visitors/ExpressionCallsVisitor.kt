/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.visitors

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.nodes.expression.Node
import io.github.subjekt.compiler.nodes.suite.Template
import io.github.subjekt.compiler.resolved.DefinedCall
import io.github.subjekt.compiler.utils.MessageCollector
import kotlin.collections.plus

/**
 * Visitor that traverses the expression and collects all the calls that are made.
 */
class ExpressionCallsVisitor(
    /**
     * Starting context for the expression.
     */
    var context: Context,
    /**
     * Message collector to report errors.
     */
    val messageCollector: MessageCollector,
) : ExpressionIrVisitor<Set<DefinedCall>> {
    private fun Node.createError(message: String) {
        messageCollector.error(message, context, this.line)
    }

    override fun visitCall(node: Node.Call): Set<DefinedCall> {
        val macro = context.lookupMacro(node.identifier)
        if (macro == null) {
            // if the macro is not found in the current context, we try to find it in the standard module
            val macro = context.lookupModule("std", node.identifier)
            if (macro == null) {
                node.createError("Macro '${node.identifier}' is not defined")
                return emptySet()
            }
            return visit(Node.DotCall("std", node.identifier, node.arguments, node.line))
        }
        if (macro.argumentsNumber != node.arguments.size) {
            node.createError(
                "Macro '${node.identifier}' expects ${macro.argumentsNumber} arguments, but got ${node.arguments.size}",
            )
            return emptySet()
        }
        val arguments = node.arguments.flatMap { arg -> visit(arg) }
        return macro.bodies
            .map { body ->
                DefinedCall(node.identifier, macro.argumentsIdentifiers, body)
            }.toSet() + arguments
    }

    override fun visitDotCall(node: Node.DotCall): Set<DefinedCall> {
        val customMacro = context.lookupModule(node.moduleId, node.callId)
        if (customMacro == null) {
            node.createError("Macro '${node.callId}' is not defined in module '${node.moduleId}'")
            return emptySet()
        }
        if (customMacro.numberOfArguments != -1 && customMacro.numberOfArguments != node.arguments.size) {
            node.createError(
                "Macro '${node.callId}' expects ${customMacro.numberOfArguments} arguments, " +
                    "but got ${node.arguments.size}",
            )
            return emptySet()
        }
        val arguments = node.arguments.flatMap { arg -> visit(arg) }
        return arguments.toSet() +
            DefinedCall("${node.moduleId}.${node.callId}", emptyList(), Template.Companion.parse(""))
    }

    override fun visitId(node: Node.Id): Set<DefinedCall> = emptySet()

    override fun visitPlus(node: Node.Plus): Set<DefinedCall> = emptySet()

    override fun visitLiteral(node: Node.Literal): Set<DefinedCall> = emptySet()
}
