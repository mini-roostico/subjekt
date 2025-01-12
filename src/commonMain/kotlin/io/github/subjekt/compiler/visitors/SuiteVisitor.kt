/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.visitors

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.compiler.nodes.suite.Macro
import io.github.subjekt.compiler.nodes.suite.Outcome
import io.github.subjekt.compiler.nodes.suite.Parameter
import io.github.subjekt.compiler.nodes.suite.Subject
import io.github.subjekt.compiler.nodes.suite.Suite
import io.github.subjekt.compiler.nodes.suite.Template
import io.github.subjekt.compiler.resolved.ResolvedSubject
import io.github.subjekt.compiler.utils.Expressions.resolveCalls
import io.github.subjekt.compiler.utils.MessageCollector
import io.github.subjekt.compiler.utils.Permutations.permute
import io.github.subjekt.compiler.utils.Permutations.permuteDefinitions
import io.github.subjekt.compiler.yaml.Reader.suiteFromYamlFile
import kotlin.collections.plusAssign

/**
 * Main visitor used by the compiler to resolve a [Suite] to a list of [ResolvedSubject]s (and therefore a
 * [io.github.subjekt.compiler.resolved.ResolvedSuite]). It must be called to visit a [Suite] node converted from the YAML
 * input one.
 */
class SuiteVisitor(
    /**
     * Message collector to report errors.
     */
    private val messageCollector: MessageCollector,
    /**
     * List of modules to register in the context. By default, all the macros not found in the current context will be
     * searched inside the `std` module, which is automatically added to this list.
     */
    private val modules: List<Any> = emptyList(),
) : SuiteIrVisitor<Unit> {
    private var context: Context = Context.Companion.emptyContext()

    /**
     * List of resolved subjects found in the suite, result of this visitor.
     */
    val resolvedSubjects = mutableSetOf<ResolvedSubject>()

//    init {
//        modules.forEach { module -> context.registerModule(module, messageCollector) }
//    }

    private fun resolveImports(imports: List<String>) {
        imports.forEach { import ->
            suiteFromYamlFile(import, messageCollector)?.let { yamlSuite ->
                val suite = Suite.Companion.fromYamlSuite(yamlSuite)
                suite.macros.forEach { visitMacro(it) }
            }
        }
    }

    override fun visitSuite(suite: Suite) {
        context.suiteName = suite.name
        context.configuration = suite.configuration
        resolveImports(suite.imports)
        suite.macros.forEach { mac -> visitMacro(mac) }
        val previousContext = context
        suite.parameters.permute { parConfiguration ->
            context = previousContext
            parConfiguration.forEach { par -> context.putParameter(par.identifier, par.value) }
            suite.subjects.forEach { sub -> visitSubject(sub) }
        }
        context = previousContext
    }

    override fun visitMacro(macro: Macro) {
        context.putMacro(macro)
    }

    override fun visitSubject(subject: Subject) {
        context.subjektName = subject.name.source
        val previousContext = context
        subject.macros.forEach { macro -> context.putMacro(macro) }
        subject.parameters.permute { parConfiguration ->
            parConfiguration.forEach { par -> context.putParameter(par.identifier, par.value) }
            subject.resolveCalls(context, messageCollector).permuteDefinitions().forEach { definedCalls ->
                val definedContext = context.withDefinedCalls(definedCalls).withParameters(parConfiguration)
                resolvedSubjects +=
                    ResolvedSubject(
                        subject.name.resolve(definedContext, messageCollector),
                        subject.code.resolve(definedContext, messageCollector),
                        subject.outcomes.map { outcome -> outcome.toResolvedOutcome(definedContext, messageCollector) },
                        subject.properties.mapValues { (_, value) -> value.resolve(definedContext, messageCollector) },
                    )
            }
        }
        context = previousContext
    }

    override fun visitOutcome(outcome: Outcome) {}

    override fun visitTemplate(template: Template) {}

    override fun visitParameter(parameter: Parameter) {}
}
