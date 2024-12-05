import java.text.SimpleDateFormat
import java.util.Date

allprojects {
  group = "io.github.freshmag"
  version = "1.0.0"

  repositories {
    mavenCentral()
  }
}

/**
 * Usage:
 * ```bash
 * ./gradlew changelog -Pversion="1.0.4" -PchangelogText="Added new feature."
 * ```
 */
tasks.register("updateVersionAndChangelog") {
  description = "Updates version in build.gradle.kts and appends a changelog entry."
  group = "Versioning"

  // Define input properties for the task
  val newVersion: String by project
  val changelogText: String by project

  doLast {
    val buildFile = file("build.gradle.kts")
    // Update the version in build.gradle.kts
    buildFile.writeText(buildFile.readText().replace("version = \"$version\"", "version = \"$newVersion\""))
    println("Version updated to $newVersion in build.gradle.kts")

    // Update the CHANGELOG.md
    val changelogFile = file("CHANGELOG.md")
    val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
    val newChangelogEntry = """
            |# [$newVersion] - $currentDate
            |${changelogText.trimIndent()}
        """.trimMargin()

    val existingChangelog = if (changelogFile.exists()) changelogFile.readText() else ""
    val updatedChangelog = "\n$newChangelogEntry\n\n$existingChangelog"
    changelogFile.writeText(updatedChangelog)
    println("Changelog updated with version $newVersion")
  }
}
