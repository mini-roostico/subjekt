plugins {
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

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}
