import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.antlr.kotlin)
  alias(libs.plugins.dokka)
  alias(libs.plugins.gitSemVer)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.kotest.multiplatform)
  alias(libs.plugins.kotlin.qa)
  alias(libs.plugins.npm.publish)
  alias(libs.plugins.multiJvmTesting)
  alias(libs.plugins.taskTree)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.serialization)
}

group = "io.github.mini-roostico"

repositories {
  google()
  mavenCentral()
}

multiJvm {
  jvmVersionForCompilation.set(21)
}

kotlin {
  jvmToolchain(21)

  jvm {
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions {
          jvmTarget = JvmTarget.JVM_1_8
        }
      }
    }
  }

  sourceSets {
    commonMain {
      kotlin {
        srcDir(layout.buildDirectory.dir("generatedAntlr"))
      }

      dependencies {
        implementation(libs.antlr.runtime)
        implementation(libs.yamlkt)
      }
    }

    commonTest.dependencies {
      implementation(libs.bundles.kotlin.testing.common)
      implementation(libs.bundles.kotest.common)
    }

    jvmTest.dependencies {
      implementation(libs.kotest.runner.junit5)
    }
  }

  js(IR) {
    moduleName = "subjekt"
    browser()
    nodejs()
    binaries.library()
  }

  applyDefaultHierarchyTemplate()

  targets.all {
    compilations.all {
      compileTaskProvider.configure {
        compilerOptions {
          allWarningsAsErrors = true
          freeCompilerArgs.add("-Xexpect-actual-classes")
        }
      }
    }
  }
}

