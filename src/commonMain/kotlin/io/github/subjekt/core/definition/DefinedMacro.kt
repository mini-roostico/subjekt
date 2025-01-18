/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.definition

import io.github.subjekt.core.Resolvable

/**
 * Represents a [io.github.subjekt.core.Macro] fixed to one of its possible values. Contrary to the original Macro, a
 * [macroId] is not unique to [io.github.subjekt.core.definition.DefinedMacro]s and can be repeated as many times as
 * the number of possible values that the original Macro can return.
 */
data class DefinedMacro(
    /**
     * Identifier of the [io.github.subjekt.core.Macro] from which this [io.github.subjekt.core.definition.DefinedMacro]
     * it has been derived.
     */
    val macroId: String,
    /**
     * The list of identifiers of the arguments that the Macro accepts. These are used to reference the arguments in the
     * Macro's resolvables.
     */
    val argumentsIdentifiers: List<String>,
    /**
     * The single [Resolvable] that the [io.github.subjekt.core.definition.DefinedMacro] can resolve. It derives from
     * one of the [Resolvable]s of the original [io.github.subjekt.core.Macro].
     */
    val value: Resolvable,
)
