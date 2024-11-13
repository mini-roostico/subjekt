package io.github.subjekt.stdlib

object Utility {

  @JvmStatic
  fun capitalizeFirst(str: String): String = str.replaceFirstChar(Char::titlecase)

}
