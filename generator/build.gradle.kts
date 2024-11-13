plugins {
    id("io.github.subjekt.kotlin-library-conventions")
    kotlin("jvm")
}

repositories {
  mavenCentral()
}

dependencies {
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
