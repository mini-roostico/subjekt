import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("io.github.subjekt.kotlin-library-conventions")
  kotlin("jvm")
  alias(libs.plugins.ktlint)
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
  mavenCentral()
}

mavenPublishing {
  coordinates("io.github.freshmag", "subjekt-api", "1.1.5")
  configure(
    KotlinJvm(
      // configures the -javadoc artifact, possible values:
      // - `JavadocJar.None()` don't publish this artifact
      // - `JavadocJar.Empty()` publish an empty jar
      // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
      javadocJar = JavadocJar.Dokka("dokkaHtml"),
      // whether to publish a sources jar
      sourcesJar = true,
    ),
  )
}

dependencies {
  implementation(project(":compiler"))

  // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
  implementation(libs.kotlin.logging)
  // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
  implementation(libs.slf4j.simple)

  implementation(libs.velocity)

  // https://mvnrepository.com/artifact/com.pinterest.ktlint/ktlint-core
  implementation("com.pinterest.ktlint:ktlint-core:0.49.1")
  // https://mvnrepository.com/artifact/com.pinterest.ktlint/ktlint-ruleset-standard
  implementation("com.pinterest.ktlint:ktlint-ruleset-standard:1.5.0")
  // https://mvnrepository.com/artifact/com.pinterest.ktlint/ktlint-rule-engine-core
  implementation("com.pinterest.ktlint:ktlint-rule-engine-core:1.5.0")
  // https://mvnrepository.com/artifact/com.pinterest.ktlint/ktlint-rule-engine
  implementation("com.pinterest.ktlint:ktlint-rule-engine:1.5.0")
  // https://mvnrepository.com/artifact/com.pinterest.ktlint/ktlint-cli-ruleset-core
  implementation("com.pinterest.ktlint:ktlint-cli-ruleset-core:1.5.0")

  testImplementation(kotlin("test"))
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_1_8)
  }
}
