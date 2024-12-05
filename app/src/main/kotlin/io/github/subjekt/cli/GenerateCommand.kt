package io.github.subjekt.cli

import picocli.CommandLine.*
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

@Command(
  name = "generate",
  description = ["Generates several kinds of file with a YAML subjekt configuration file."],
  subcommands = [
    HelpCommand::class,
    GenerateSourcesSubcommand::class,
  ]
)
class GenerateCommand: Runnable {
  override fun run() {
    println("Type 'help' to see available commands")
  }
}

@Command(
  name = "sources",
  description = ["Generate several Kotlin sources depending on the specified YAML subjekt file"]
)
class GenerateSourcesSubcommand : Runnable {
  @Parameters(
    description = ["Path of the YAML subjekt file"],
  )
  lateinit var path: String

  @Option(
    names = ["-o", "--output-dir"],
    description = ["Output path of the generated Kotlin sources"],
  )
  var outPath: String = "./"

  override fun run() {
    Path(outPath).createDirectories()
    TODO()
  }
}
