package io.github.subjekt.cli

import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand

@Command(
  name = "",
  subcommands = [
    HelpCommand::class,
    GenerateCommand::class
  ],
  description = ["Subjekt CLI"],
  version = [
    "@|yellow Subjekt-CLI |@",
    "@|blue Build 1.0|@",
    "@|red,bg(white) (c) 2024|@"],
)
class EntryPointCommand : Runnable {
  override fun run() {
    println("Type 'help' to see available commands")
  }
}
