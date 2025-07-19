package io.github.subjekt.engine

import io.github.subjekt.TestingUtility.compileYaml
import io.github.subjekt.TestingUtility.resolve
import io.github.subjekt.core.resolution.ResolvedSuite
import io.github.subjekt.core.value.FloatValue
import io.github.subjekt.core.value.Value
import io.github.subjekt.core.value.Value.Companion.asBooleanValue
import io.github.subjekt.core.value.Value.Companion.asDoubleValue
import io.github.subjekt.core.value.Value.Companion.asIntValue
import io.github.subjekt.core.value.Value.Companion.asStringValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class TypeCoherenceSpec : FunSpec({

    fun expr(s: String): String = "\${{$s}}"

    fun String.evaluate(): ResolvedSuite = SubjektEngine.Default.evaluate(this.trimMargin().compileYaml())

    fun typeCoherenceParameter(
        valuesYaml: List<Any>,
        valueSubjekt: List<Value>,
    ) {
        require(valuesYaml.isNotEmpty() && valuesYaml.size == valueSubjekt.size)
        val suite =
            """
            |name: test
            |parameters:
            |  - name: param1
            |    values: [${valuesYaml.joinToString(", ")}]
            |subjects:
            |  - "${expr("param1")}"
            """.trimMargin().compileYaml()
        suite.symbolTable.resolveParameter("param1")?.values?.forEachIndexed { index, value ->
            println("Resolved value ${value::class.simpleName} - $value")
            println("Expected value ${valueSubjekt[index]::class.simpleName} - ${valueSubjekt[index]}")
            if (value is FloatValue) {
                // for tolerance in float comparison
                value.value shouldBe (valueSubjekt[index].castToFloat().value plusOrMinus 0.001)
            } else {
                value shouldBe valueSubjekt[index]
            }
        }
    }

    fun typeCoherenceMacros(
        arg: String,
        valuesYaml: List<String>,
        valueSubjekt: List<Value>,
    ) {
        require(valuesYaml.isNotEmpty() && valuesYaml.size == valueSubjekt.size)
        val suite =
            """
            |name: test
            |macros:
            |  - def: m1(arg)
            |    values: [${valuesYaml.joinToString(", ") { "\"$it\"" }}]
            |subjects:
            |  - "${expr("m1($arg)")}"
            """.trimMargin().compileYaml()
        suite.resolve().resolvedSubjects.map { it.name!!.value }.map {
            if (it is FloatValue) {
                FloatValue(kotlin.math.round(it.value * 10000) / 10000) // for tolerance in float comparison
            } else {
                it
            }
        } shouldBe valueSubjekt
    }

    forAll(
        row("string", listOf("a", "b", "c"), listOf("a", "b", "c").map { it.asStringValue() }),
        row("integer", listOf(1, 2, 3), listOf(1, 2, 3).map { it.asIntValue() }),
        row("boolean", listOf(true, false), listOf(true, false).map { it.asBooleanValue() }),
        row(
            "float",
            listOf("1.5", "2.7", "3.9"),
            listOf(1.5, 2.7, 3.9).map { it.asDoubleValue() },
        ),
    ) { type, valuesYaml, valueSubjekt ->
        test("type coherence for $type inside parameters") {
            typeCoherenceParameter(valuesYaml, valueSubjekt)
        }
    }

    forAll(
        row(
            "string",
            "'argument'",
            listOf("arg .. ' ' .. 'test'", "'second ' .. arg").map { expr(it) },
            listOf("argument test", "second argument").map { it.asStringValue() },
        ),
        row(
            "integer",
            "1",
            listOf("arg + 1", "arg * 5").map { expr(it) },
            listOf(2, 5).map { it.asIntValue() },
        ),
// NOT SUPPORTED YET TODO
//        row(
//            "boolean",
//            "true",
//            listOf(""),
//            listOf(true, false).map { it.asBooleanValue() }
//        ),
        row(
            "float",
            "1.5",
            listOf("arg + 1.5", "arg * 2.2").map { expr(it) },
            listOf(3.0, 3.3).map { it.asDoubleValue() },
        ),
    ) { type, argument, valuesYaml, valueSubjekt ->
        test("type coherence for $type inside macros") {
            typeCoherenceMacros(argument, valuesYaml, valueSubjekt)
        }
    }
})
