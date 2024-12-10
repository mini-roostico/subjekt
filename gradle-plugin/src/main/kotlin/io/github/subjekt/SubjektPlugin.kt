package io.github.subjekt

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toFiles
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateSubjektFilesExtension {
  var inputPaths: List<File> = emptyList()
  var outputDir: File? = null
}

abstract class GenerateSubjektFilesTask : DefaultTask() {
  @Input
  lateinit var inputPaths: List<File>

  @OutputDirectory
  lateinit var outputDir: File

  @TaskAction
  fun generateFiles() {
    println("Processing files from: $inputPaths")
    println("Output directory: $outputDir")

    subjekt {
      inputPaths.forEach { file ->
        if (file.isDirectory) {
          addSourceDir(file)
        } else {
          addSource(file)
        }
      }
    }.toFiles(outputDir.absolutePath, "kt")

    println("File generation complete.")
  }
}

class SubjektPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    // Create the extension for user configuration
    val extension = project.extensions.create("subjekt", GenerateSubjektFilesExtension::class.java)

    project.tasks.register("generateSubjektFiles", GenerateSubjektFilesTask::class.java) {
      group = "generation"
      description = "Generates files from the specified directories or files."

      inputPaths = extension.inputPaths
      outputDir = extension.outputDir
        ?: throw IllegalArgumentException("Output directory must be specified in the 'generateSubjekt' extension.")
    }

  }
}
