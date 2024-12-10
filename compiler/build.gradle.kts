import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
  id("io.github.subjekt.kotlin-library-conventions")
  kotlin("jvm")
  antlr
  alias(libs.plugins.ktlint)
  alias(libs.plugins.dokka)
  alias(libs.plugins.mavenPublish)
}

repositories {
  mavenCentral()
}

sourceSets {
  main {
    java {
      srcDir(tasks.generateGrammarSource)
    }
  }
}

mavenPublishing {
  coordinates("io.github.freshmag", "subjekt-compiler", "1.1.1")
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
  // https://mvnrepository.com/artifact/org.antlr/antlr4
  antlr(libs.antlr)
  // https://mvnrepository.com/artifact/org.antlr/antlr4-runtime
  runtimeOnly(libs.antlr.runtime)

  // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
  implementation(libs.jackson)
  // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-yaml
  implementation(libs.jackson.yaml)

  testImplementation(kotlin("test"))
}

tasks.generateGrammarSource {
  source("src/main/antlr/io/github/subjekt")
  arguments = listOf("-visitor")
}

tasks.processResources {
  exclude("**/*.java")
}

tasks.named("runKtlintCheckOverTestSourceSet") {
  dependsOn("generateTestGrammarSource")
}

tasks.named("compileTestKotlin") {
  dependsOn("generateTestGrammarSource")
}

tasks.named<Jar>("sourcesJar") {
  archiveClassifier.set("sources")
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  from(sourceSets.main.get().allSource)
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}
