plugins {
  alias(libs.plugins.ktlint)
  `java-gradle-plugin`
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "1.2.1"
}

group = "io.github.freshmag"
version = "1.0.0"

publishing {
  repositories {
    maven {
      url = uri("/Users/fresh/.m2/repository")
    }
  }
}

gradlePlugin {
  website = "https://freshmag.github.io/subjekt-doc/"
  vcsUrl = "https://github.com/FreshMag/subjekt"
  plugins {
    create("io.github.subjekt") {
      id = "io.github.subjekt"
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


kotlin {
  jvmToolchain(17)
}
