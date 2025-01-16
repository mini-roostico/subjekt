/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt

import io.kotest.assertions.fail

object TestingUtility {
    /**
     * Utility function to get the value of a Result, printing the exception if the result is a failure.
     */
    fun <T> Result<T>.getOrFail(): T {
        if (this.isFailure) {
            this.exceptionOrNull()?.printStackTrace()
            fail("This exception originated the failure:\n\n${this.exceptionOrNull()?.message}")
        }
        return this.getOrNull()!!
    }
}
