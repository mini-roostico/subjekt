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

  testImplementation(kotlin("test"))
}

tasks.generateGrammarSource {
  source("src/main/antlr/io/github/subjekt")
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}
