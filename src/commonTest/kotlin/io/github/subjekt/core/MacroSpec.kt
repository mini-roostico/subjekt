/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.core

import io.github.subjekt.core.Macro.Companion.asMacro
import io.github.subjekt.core.Macro.Companion.asMacroDefinition
import io.github.subjekt.core.Macro.Companion.toActualMacro
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class MacroSpec : StringSpec({

    "Macro should be created correctly from MacroDefinition" {
        val macroDefinition = "macroId(arg1, arg2)".asMacroDefinition()
        val resolvables = listOf(Resolvable("value1"), Resolvable("value2"))
        val macro = macroDefinition.toActualMacro(resolvables)

        macro.id shouldBe "macroId"
        macro.argumentsIdentifiers shouldBe listOf("arg1", "arg2")
        macro.resolvables shouldBe resolvables
    }

    "Macro should be created correctly from string ID" {
        val resolvables = listOf(Resolvable("value1"), Resolvable("value2"))
        val macro = "macroId".asMacro(resolvables)

        macro.id shouldBe "macroId"
        macro.argumentsIdentifiers shouldBe emptyList()
        macro.resolvables shouldBe resolvables
    }

    "Macro equality should be based on id and number of arguments" {
        val macro1 = Macro("macroId", listOf("arg1", "arg2"), listOf(Resolvable("value1")))
        val macro2 = Macro("macroId", listOf("arg1", "arg2"), listOf(Resolvable("value2")))
        val macro3 = Macro("macroId", listOf("arg1"), listOf(Resolvable("value1")))

        macro1 shouldBe macro2
        macro1 shouldNotBe macro3
    }

    "Macro hashCode should be based on id and number of arguments" {
        val macro1 = Macro("macroId", listOf("arg1", "arg2"), listOf(Resolvable("value1")))
        val macro2 = Macro("macroId", listOf("arg1", "arg2"), listOf(Resolvable("value2")))

        macro1.hashCode() shouldBe macro2.hashCode()
    }

    "String should be converted to MacroDefinition correctly" {
        val macroDefinition = "macroId(arg1, arg2)".asMacroDefinition()

        macroDefinition.first shouldBe "macroId"
        macroDefinition.second shouldBe listOf("arg1", "arg2")
    }

    "String without arguments should be converted to MacroDefinition correctly" {
        val macroDefinition = "macroId".asMacroDefinition()

        macroDefinition.first shouldBe "macroId"
        macroDefinition.second shouldBe emptyList()
    }

    "Illegal macro definition without closing parenthesis should throw an exception" {
        val illegalMacro = "macroId(arg1, arg2"
        val exception =
            shouldThrow<IllegalArgumentException> {
                illegalMacro.asMacroDefinition()
            }
        exception.message shouldBe "Illegal macro definition. Expected ')' in macroId(arg1, arg2"
    }

    "Illegal macro definition with illegal characters should throw an exception" {
        val illegalMacro = "macroId(arg1, arg2, arg3$)"
        val exception =
            shouldThrow<IllegalArgumentException> {
                illegalMacro.asMacroDefinition()
            }
        exception.message shouldBe "Macro definition can contain alphanumeric characters or one of the following: _(),"
    }
})
