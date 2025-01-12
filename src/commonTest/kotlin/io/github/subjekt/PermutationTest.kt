package io.github.subjekt

import io.github.subjekt.nodes.suite.Parameter
import io.github.subjekt.utils.Permutations.permute
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PermutationTest {

  @Test
  fun `Simple parameter permutation`() {
    val par1 = Parameter("test", listOf("1", "2"))
    val par2 = Parameter("test2", listOf("3", "4"))
    val result = mutableSetOf<String>()
    listOf(par1, par2).permute {
      result.add(it.joinToString(separator = "") { it.value.toString() })
    }
    val expected = setOf("13", "14", "23", "24")
    assertEquals(expected, result)
  }

  @Test
  fun `One single parameter list`() {
    val par1 = Parameter("test", listOf("1", "2"))
    val par2 = Parameter("test2", listOf("3"))
    val result = mutableSetOf<String>()
    listOf(par1, par2).permute {
      result.add(it.joinToString(separator = "") { it.value.toString() })
    }
    val expected = setOf("13", "23")
    assertEquals(expected, result)
  }

  @Test
  fun `Both single parameter list`() {
    val par1 = Parameter("test", listOf("1"))
    val par2 = Parameter("test2", listOf("3"))
    val result = mutableSetOf<String>()
    listOf(par1, par2).permute {
      result.add(it.joinToString(separator = "") { it.value.toString() })
    }
    val expected = setOf("13")
    assertEquals(expected, result)
  }

  @Test
  fun `Simple permutations of Iterable-Iterable-String`() {
    val list = listOf(listOf(1, 2), listOf(3, 4, 5))
    val expected = listOf(
      listOf(1, 3),
      listOf(1, 4),
      listOf(1, 5),
      listOf(2, 3),
      listOf(2, 4),
      listOf(2, 5),
    )
    assertEquals(expected, list.permute())
  }
}
