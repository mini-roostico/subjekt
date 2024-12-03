package io.github.subjekt.tests

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class KotestGenerator(val preamble: String = "") : TestGenerator {

  private fun writeTestFile(className: String, path: Path, testContent: String, outputPackage: String) {
    Files.createDirectories(path.parent)
    val content = """
        package $outputPackage
        import io.kotest.core.spec.style.FreeSpec    
        import org.unibo.plugintest.CompileUtils.KotlinTestingProgram
        import org.unibo.plugintest.CompileUtils.noWarning
        import org.unibo.plugintest.CompileUtils.warning
        import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
        
        @OptIn(ExperimentalCompilerApi::class)
        class $className : FreeSpec({
          "Iterated use of aggregate functions should result in warnings if manual 'alignedOn' is missing" - {
            $testContent
          }
        })
    """.trimIndent()

    Files.write(path, content.toByteArray(), StandardOpenOption.CREATE)
    println("All tests written to $path")
  }
}
