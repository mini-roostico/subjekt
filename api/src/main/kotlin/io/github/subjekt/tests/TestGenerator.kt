package io.github.subjekt.tests

import io.github.subjekt.resolved.SubjektSuite

interface TestGenerator {

  fun SubjektSuite.generateTests(): String
}
