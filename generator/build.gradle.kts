plugins {
    id("io.github.subjekt.kotlin-library-conventions")
    kotlin("jvm")
    antlr
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

dependencies {
  // https://mvnrepository.com/artifact/org.antlr/antlr4
  antlr("org.antlr:antlr4:4.13.2")
  // https://mvnrepository.com/artifact/org.antlr/antlr4-runtime
  runtimeOnly("org.antlr:antlr4-runtime:4.13.2")

}

tasks.generateGrammarSource {
  source("src/main/antlr/io/github/subjekt")
  outputDirectory = file("${buildDir}/generated/sources/main/java/antlr")
}
