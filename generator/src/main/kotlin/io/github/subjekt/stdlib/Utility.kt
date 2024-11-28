package io.github.subjekt.stdlib

import kotlin.math.min

object Utility {

  private fun String.substringStartingFromFirstValidChar(): String {
    val startIndex = indexOfFirst { it.isLetter() }
    return if (startIndex != -1) substring(startIndex) else ""
  }

  private fun String.substringUntilFirstInvalidChar(): String {
    val startIndex = indexOfFirst { !it.isLetter() }
    return if (startIndex != -1) substring(0, startIndex) else ""
  }

  @JvmStatic
  fun capitalizeFirst(str: String): String = str.replaceFirstChar(Char::titlecase)

  @JvmStatic
  fun idFromCode(code: String, maxLength: Int = 20): String =
    code.substringStartingFromFirstValidChar().trim().substringUntilFirstInvalidChar().trim().run {
      substring(0, min(maxLength, length))
    }.replace("[^a-zA-Z0-9]".toRegex(), "").replaceFirstChar(Char::titlecase)

  @JvmStatic
  fun prettify(vararg parametersValues: Any): String =
    parametersValues.joinToString("") { idFromCode(it.toString()) }

}
