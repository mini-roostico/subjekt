/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.permutations

import io.github.subjekt.core.SubjektFunction
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.definition.DefinedMacro
import io.github.subjekt.core.definition.DefinedParameter

/**
 * Generates all the possible [Context] permutations out of the given [parameters], [macros], and [functions].
 * This is useful to identify which contexts are needed to resolve all the possible values out of a
 * [io.github.subjekt.core.Subject]'s resolvables.
 *
 * Note: functions are not used to generate the permutations, but they are included in the output.
 */
internal fun contextPermutationsOutOf(
    parameters: Set<List<DefinedParameter>>,
    macros: Set<List<DefinedMacro>>,
    functions: Set<SubjektFunction>,
): List<Context> {
    val parameterCombinations = generateCombinations(parameters)
    val macroCombinations = generateCombinations(macros)

    return parameterCombinations.flatMap { paramCombo ->
        macroCombinations.map { macroCombo ->
            Context(
                definedParameters = paramCombo.associateBy { it.parameterId },
                definedMacros = macroCombo.associateBy { it.macroId },
                functions = functions.associateBy { it.id },
            )
        }
    }
}

/**
 * Generates all the possible combinations of the lists inside the given [set].
 *
 * That is, if the input is:
 * ```kotlin
 * setOf(listOf(1, 2), listOf(3, 4))
 * ```
 * the output will be:
 * ```kotlin
 * listOf(listOf(1, 3), listOf(1, 4), listOf(2, 3), listOf(2, 4))
 * ```
 */
private fun <T> generateCombinations(set: Set<List<T>>): List<List<T>> {
    if (set.isEmpty()) return listOf(emptyList())
    return set.fold(listOf(listOf<T>())) { acc, list ->
        acc.flatMap { combination ->
            list.map { combination + it }
        }
    }
}
