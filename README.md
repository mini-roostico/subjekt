<p align="center">
    <img width=60% src="resources/img/logo.png">
</p>
<p align="center">
    <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white"></a>  
    <a href="https://github.com/mini-roostico/subjekt/blob/master/LICENSE"><img src="https://img.shields.io/github/license/FreshMag/subjekt.svg?style=flat"></a>
    <a href="https://conventionalcommits.org"><img src="https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits"></a>
    <a href="https://github.com/mini-roostico/subjekt/actions"><img src="https://github.com/mini-roostico/subjekt/actions/workflows/dispatcher.yml/badge.svg"></a>
    <img src="https://img.shields.io/maven-central/v/io.github.mini-roostico/subjekt" >
    <img src="https://img.shields.io/npm/v/@mini-roostico/subjekt">
    <a href="https://ktlint.github.io/"><img src="https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg"></a>
    <img src="https://img.shields.io/github/repo-size/mini-roostico/subjekt">
</p>

## Overview

Subjekt is a Kotlin Multiplatform library to generate permutations of results from a YAML/JSON configuration. 

It can be configured to handle multiple parameters and utility functions and generate all the possible permutations of their values when they are used in the code.

### About the current version

At the moment, Subjekt has Kotlin Multiplatform has a primary target, but before version `2.0.0` it was a JVM only library.

Some of the platform-specific functionalities that were previously available could be missing right now, for example **external modules** and automatic code linting via `ktlint`. 

In future updates these will be made available on all platforms.

## Quick start

You can include Subjekt as a maven dependency by simply adding this to your `build.gradle.kts`:

```kotlin
implementation("io.github.subjekt:subjekt:<LATEST_VERSION>")
```

Or, if you want, you can also use it in your node project by running:

```bash
npm i @mini-roostico/subjekt
```

To learn how to use Subjekt, please take a look at [the website](https://mini-roostico.github.io/subjekt-doc/)
