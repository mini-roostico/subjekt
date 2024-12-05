package io.github.subjekt.files

object Utils {

  fun cleanName(name: String): String = name.replace(Regex("[^A-Za-z0-9 ]"), "")
}
