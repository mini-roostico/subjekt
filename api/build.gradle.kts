import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
  id("io.github.subjekt.kotlin-library-conventions")
  kotlin("jvm")
  alias(libs.plugins.mavenPublish)
  alias(libs.plugins.dokka)
}

repositories {
  mavenCentral()
}

mavenPublishing {
  configure(
    KotlinJvm(
      // configures the -javadoc artifact, possible values:
      // - `JavadocJar.None()` don't publish this artifact
      // - `JavadocJar.Empty()` publish an empty jar
      // - `JavadocJar.Dokka("dokkaHtml")` when using Kotlin with Dokka, where `dokkaHtml` is the name of the Dokka task that should be used as input
      javadocJar = JavadocJar.Dokka("dokkaHtml"),
      // whether to publish a sources jar
      sourcesJar = true,
    )
  )
}

dependencies {
  implementation(project(":compiler"))

  // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
  implementation(libs.jackson)
  // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml
  implementation(libs.jackson.yaml)
  // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
  implementation(libs.kotlin.logging)
  // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
  implementation(libs.slf4j.simple)

  implementation(libs.velocity)

  testImplementation(kotlin("test"))
}
