/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core.resolution

import io.github.subjekt.core.Resolvable
import io.github.subjekt.core.value.StringValue
import io.github.subjekt.core.value.Value
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * Represents a [Resolvable] that has been resolved to a single value by fixing the
 * [io.github.subjekt.core.definition.Context].
 */
@OptIn(ExperimentalJsExport::class)
@JsExport
data class Instance(
    /**
     * The resolved value of the [Resolvable].
     */
    val value: Value,
    /**
     * The original [Resolvable] from which this [Instance] was resolved.
     */
    val origin: Resolvable,
) {
    @JsName("fromString")
    constructor(string: String, origin: Resolvable) : this(
        StringValue(string),
        origin,
    )
}
