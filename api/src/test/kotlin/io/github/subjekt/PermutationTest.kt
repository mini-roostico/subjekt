package io.github.subjekt

import io.github.subjekt.files.Parameter
import io.github.subjekt.rendering.Permutations.permute
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PermutationTest {

  @Test
  fun `Lists of Lists of string permutations`() {
    val input = listOf(listOf("1", "2", "3"), listOf("1", "2"))
    val expected = listOf("11", "12", "21", "22", "31", "32")
    assertEquals(expected, input.permute())
  }

  @Test
  fun `Lists of Lists of string permutations with separator`() {
    val input = listOf(listOf("1", "2", "3"), listOf("1", "2"))
    val expected = listOf("1 1", "1 2", "2 1", "2 2", "3 1", "3 2")
    assertEquals(expected, input.permute(" "))
  }

  @Test
  fun `List of parameters`() {
    val input = listOf(
      Parameter("par1", listOf(1, 2, 3)),
      Parameter("par2", listOf("a", "b")),
    )
    val expected = listOf(
      mapOf("par1" to 1, "par2" to "a"),
      mapOf("par1" to 1, "par2" to "b"),
      mapOf("par1" to 2, "par2" to "a"),
      mapOf("par1" to 2, "par2" to "b"),
      mapOf("par1" to 3, "par2" to "a"),
      mapOf("par1" to 3, "par2" to "b"),
    )
    assertEquals(expected, input.permute())
  }
}
