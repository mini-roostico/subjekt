package io.github.subjekt.conversion

import kotlin.math.min

@SubjektModule("std")
object Stdlib {

  @JvmStatic
  @Macro("capitalizeFirst")
  fun capitalizeFirst(str: String): List<String> = listOf(str.replaceFirstChar(Char::titlecase))

  @JvmStatic
  @Macro("prettify")
  fun prettify(vararg parametersValues: String): List<String> = listOf(parametersValues.joinToString("") { idFromCode(it) })

  private fun String.substringStartingFromFirstValidChar(): String {
    val startIndex = indexOfFirst { it.isLetter() }
    return if (startIndex != -1) substring(startIndex) else ""
  }

  private fun String.substringUntilFirstInvalidChar(): String {
    val endIndex = indexOfFirst { !it.isLetter() }
    return if (endIndex != -1) substring(0, endIndex) else this
  }

  private fun idFromCode(code: String, maxLength: Int = 20): String = code
    .substringStartingFromFirstValidChar()
    .trim()
    .substringUntilFirstInvalidChar()
    .trim()
    .run {
      substring(0, min(maxLength, length))
    }
    .replace("[^a-zA-Z0-9]".toRegex(), "")
    .replaceFirstChar(Char::titlecase)
}
