package io.github.subjekt.files

/**
 * Utility functions for file handling.
 */
object Utils {
  /**
   * Cleans a name by removing all non-alphanumeric characters.
   */
  fun cleanName(name: String): String = name.replace(Regex("[^A-Za-z0-9 ]"), "")
}
