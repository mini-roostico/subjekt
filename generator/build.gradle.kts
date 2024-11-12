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


}
