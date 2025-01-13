/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.utils

import io.github.subjekt.compiler.nodes.suite.Parameter
import io.github.subjekt.compiler.resolved.DefinedCall
import io.github.subjekt.compiler.resolved.ResolvedParameter

/**
 * Utility object for generating permutations.
 */
object Permutations {
    /**
     * Generates all possible permutations of the given [List] of [Parameter]s and calls the [parameterConfigurationConsumer]
     * for each permutation.
     */
    fun List<Parameter>.permute(parameterConfigurationConsumer: (List<ResolvedParameter>) -> Unit) {
        val cartesianProduct =
            this.map { it.values }.fold(sequenceOf(emptyList<Any>())) { acc, values ->
                acc.flatMap { combination ->
                    values.asSequence().map { combination + it }
                }
            }

        cartesianProduct
            .map { combination ->
                combination.mapIndexed { index, value ->
                    ResolvedParameter(this[index].name, value)
                }
            }.forEach {
                parameterConfigurationConsumer(it)
            }
    }

    /**
     * Generates all possible permutations of the given [List] of [List]s and returns them.
     * For example: `[["a", "b"], ["1", "2"]]` will return `[["a", "1"], ["a", "2"], ["b", "1"], ["b", "2"]]`.
     */
    fun <T> Iterable<Iterable<T>>.permute(): Iterable<Iterable<T>> =
        fold(listOf(emptyList<T>()) as Iterable<List<T>>) { acc, iterable ->
            acc.flatMap { combination ->
                iterable.map { element ->
                    combination + element
                }
            }
        }

    /**
     * Generated all possible permutations for the given definitions. Returns an [Iterable] of [Iterable]s where each
     * corresponds to a possible instance of definitions that can be used for a unique context.
     */
    fun Iterable<DefinedCall>.permuteDefinitions(): Iterable<Iterable<DefinedCall>> =
        this.groupBy(DefinedCall::identifier).values.permute()
}
