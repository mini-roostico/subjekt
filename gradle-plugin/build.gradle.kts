plugins {
  alias(libs.plugins.ktlint)
  `java-gradle-plugin`
  `kotlin-dsl`
  alias(libs.plugins.gradle.publish)
}

group = "io.github.freshmag"
version = "1.0.1"

gradlePlugin {
  website = "https://freshmag.github.io/subjekt-doc/"
  vcsUrl = "https://github.com/FreshMag/subjekt"
  plugins {
    create("subjektPlugin") {
      id = "io.github.freshmag.subjekt"
      displayName = "Subjekt"
      description = "A Gradle plugin for generating Kotlin code and tests from YAML configuration files"
      tags = listOf("generation", "test", "auto-generated")
      implementationClass = "io.github.subjekt.SubjektPlugin"
    }
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(project(":api"))
}
