/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler

import io.github.subjekt.compiler.permutations.requestNeededContexts
import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Suite
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.resolution.Instance
import io.github.subjekt.core.resolution.ResolvedSubject
import io.github.subjekt.core.resolution.ResolvedSuite

/**
 * Resolves this Suite into a [ResolvedSuite] object containing all the resolved Subjects.
 */
fun Suite.resolve(): ResolvedSuite =
    ResolvedSuite(
        this,
        this.subjects.flatMap { it.resolve() }.toSet(),
    )

/**
 * Resolves this Subject into a set of [ResolvedSubject] objects, one for each context needed by the Subject
 * (i.e. one for each permutation of the symbols' values it uses).
 */
fun Subject.resolve(): Set<ResolvedSubject> {
    val neededContexts = this.requestNeededContexts()
    return neededContexts
        .map { context ->
            ResolvedSubject(
                this.id,
                this.resolvables.resolve(context),
            )
        }.toSet()
}

/**
 * Resolves this map of [Resolvable] objects into a map of [Instance] objects, using the given [context].
 */
fun Map<String, Resolvable>.resolve(context: Context): Map<String, Instance> =
    this.mapValues { Instance(it.value.resolve(context), it.value) }
