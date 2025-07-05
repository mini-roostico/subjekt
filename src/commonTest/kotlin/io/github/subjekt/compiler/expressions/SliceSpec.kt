package io.github.subjekt.compiler.expressions

import io.github.subjekt.TestingUtility.shouldResolveToSubjects
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.SymbolTable
import io.kotest.core.spec.style.StringSpec

class SliceSpec : StringSpec({
    "Simple single slice" {
        val symbolTable =
            SymbolTable(
                mapOf("arr" to Parameter("arr", listOf("a", "b", "c"))),
            )
        "\${{ arr[1] }}".shouldResolveToSubjects(
            symbolTable,
            "b",
        )
    }

    "Single slice with expression" {
        val symbolTable =
            SymbolTable(
                mapOf(
                    "arr" to Parameter("arr", listOf("a", "b", "c")),
                    "index" to Parameter("index", listOf("1", "2")),
                ),
            )

        "\${{ arr[(4 * 3)%2 + index] }}".shouldResolveToSubjects(
            symbolTable,
            "b",
            "c",
        )
    }
})
