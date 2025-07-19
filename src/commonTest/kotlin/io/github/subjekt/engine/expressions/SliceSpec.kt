package io.github.subjekt.engine.expressions

import io.github.subjekt.TestingUtility.shouldResolveToSubjects
import io.github.subjekt.core.Parameter
import io.github.subjekt.core.SymbolTable
import io.github.subjekt.core.value.Value.Companion.asStringValue
import io.github.subjekt.engine.expressions.slices.SliceEngine.slice
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SliceSpec : StringSpec({
    val symbolTable =
        SymbolTable(
            mapOf(
                "arr" to Parameter("arr", listOf("a", "b", "c", "d", "e").map { it.asStringValue() }),
                "index" to Parameter("index", listOf("1".asStringValue(), "2".asStringValue())),
            ),
        )

    "Simple single slice" {
        "\${{ arr[1] }}".shouldResolveToSubjects(
            symbolTable,
            "b",
        )
    }

    "Single slice with expression" {
        "\${{ arr[(4 * 3)%2 + index] }}".shouldResolveToSubjects(
            symbolTable,
            "b",
            "c",
        )
    }

    "Slice with start and end indices" {
        "\${{ arr[1:4] }}".shouldResolveToSubjects(
            symbolTable,
            "b",
            "c",
            "d",
        )
    }

    "Slice with start, end, and step indices" {
        "\${{ arr[0:5:2] }}".shouldResolveToSubjects(
            symbolTable,
            "a",
            "c",
            "e",
        )
    }

    "Slice with complex start and end expressions" {
        "\${{ arr[(2 * 1):(3 + 1)] }}".shouldResolveToSubjects(
            symbolTable,
            "c",
            "d",
        )
    }

    "Slice with complex step expression" {
        "\${{ arr[0:5:2] }}".shouldResolveToSubjects(
            symbolTable,
            "a",
            "c",
            "e",
        )
    }

    "Slice with negative indices" {
        "\${{ arr[-3:-1] }}".shouldResolveToSubjects(
            symbolTable,
            "c",
            "d",
        )
    }

    "Slice with negative step" {
        "\${{ arr[4:0:-1] }}".shouldResolveToSubjects(
            symbolTable,
            "e",
            "d",
            "c",
            "b",
        )
    }

    "Slice without end index" {
        "\${{ arr[2:] }}".shouldResolveToSubjects(
            symbolTable,
            "c",
            "d",
            "e",
        )
    }

    "Slice without start index" {
        "\${{ arr[:3] }}".shouldResolveToSubjects(
            symbolTable,
            "a",
            "b",
            "c",
        )
    }

    "Slice with start and step indices" {
        "\${{ arr[1::2] }}".shouldResolveToSubjects(
            symbolTable,
            "b",
            "d",
        )
    }

    "Slice with end and step indices" {
        "\${{ arr[:4:2] }}".shouldResolveToSubjects(
            symbolTable,
            "a",
            "c",
        )
    }

    "Double slice" {
        "\${{ arr[1:3] .. '-' .. arr[2:4] }}".shouldResolveToSubjects(
            symbolTable,
            "b-c",
            "b-d",
            "c-c",
            "c-d",
        )
    }

    "List slice" {
        listOf(1, 2, 3).slice(1, 2) shouldBe listOf(2)
        listOf(1, 2, 3).slice(1, 3) shouldBe listOf(2, 3)
        listOf(1, 2, 3).slice(0, 2) shouldBe listOf(1, 2)
        listOf(1, 2, 3).slice(0, 3) shouldBe listOf(1, 2, 3)
        listOf(1, 2, 3).slice(1, 3, 2) shouldBe listOf(2)
        listOf(1, 2, 3, 4, 5).slice(3, 1, -2) shouldBe listOf(4)
        listOf(1, 2, 3, 4, 5).slice(4, 0, -2) shouldBe listOf(5, 3)
        listOf(1, 2, 3, 4, 5).slice(0, 5, 2) shouldBe listOf(1, 3, 5)
        listOf(1, 2, 3, 4, 5).slice(0, 5, -2) shouldBe emptyList()
        listOf(1, 2, 3, 4, 5).slice(0, null, 2) shouldBe listOf(1, 3, 5)
        listOf(1, 2, 3, 4, 5).slice(0, null, -2) shouldBe listOf(1)
        listOf(1, 2, 3, 4, 5).slice(null, 3, 2) shouldBe listOf(1, 3)
        listOf(1, 2, 3, 4, 5).slice(null, 3, -2) shouldBe listOf(5)
        listOf(1, 2, 3, 4, 5).slice(null, null, 2) shouldBe listOf(1, 3, 5)
        listOf(1, 2, 3, 4, 5).slice(null, null, -2) shouldBe listOf(5, 3, 1)
    }
})
