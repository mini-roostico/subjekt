package io.github.subjekt.engine.impl

import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.Subject
import io.github.subjekt.core.Suite
import io.github.subjekt.core.definition.Context
import io.github.subjekt.core.resolution.Instance
import io.github.subjekt.core.resolution.ResolvedSubject
import io.github.subjekt.core.resolution.ResolvedSuite
import io.github.subjekt.engine.SubjektEngine
import io.github.subjekt.engine.permutations.requestNeededContexts

class SubjektEngineImpl : SubjektEngine {
    /**
     * Resolves this Suite into a [ResolvedSuite] object containing all the resolved Subjects.
     */
    private fun Suite.resolve(): ResolvedSuite =
        ResolvedSuite(
            this,
            subjects.flatMap { it.resolve() }.toSet(),
        )

    /**
     * Resolves this Subject into a set of [ResolvedSubject] objects, one for each context needed by the Subject
     * (i.e. one for each permutation of the symbols' values it uses).
     */
    private fun Subject.resolve(): Set<ResolvedSubject> {
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
    private fun Map<String, Resolvable>.resolve(context: Context): Map<String, Instance> =
        this.mapValues { Instance(it.value.resolve(context), it.value) }

    override fun evaluate(suite: Suite): ResolvedSuite = suite.resolve()
}
