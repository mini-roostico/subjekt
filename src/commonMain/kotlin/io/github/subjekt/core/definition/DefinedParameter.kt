/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.definition

/**
 * Represents a parameter fixed to a value. Contrary to the original Parameter, a [parameterId] is not unique to
 * [DefinedParameter]s and can be repeated as many times as the number of the possible values of the original Parameter.
 */
data class DefinedParameter(
    /**
     * Identifier of the [io.github.subjekt.core.Parameter] from which this [DefinedParameter] it has been derived.
     */
    val parameterId: String,
    /**
     * Value of this [DefinedParameter]. It derives from one of the possible values of the original
     * [io.github.subjekt.core.Parameter].
     */
    val value: Any,
)
