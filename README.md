<p align="center"><img width=60% src="resources/img/logo.png"></p>


&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
[![Language][kotlin-shield]][kotlin-url]
[![MIT License][license-shield]][license-url]
[![Conventional Commits][conventional-commits-shield]][conventional-commits-url]


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
implementation("io.github.mini-roostico:subjekt:<LATEST_VERSION>")
```

Or, if you want, you can also use it in your node project by running:

```bash
npm i @mini-roostico/subjekt
```

To learn how to use Subjekt, please take a look at [the website](https://mini-roostico.github.io/subjekt-doc/)

<!--
***
    GITHUB SHIELDS VARIABLES
***
-->

[kotlin-shield]: https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white

[kotlin-url]: https://kotlinlang.org/

[license-shield]: https://img.shields.io/github/license/FreshMag/subjekt.svg?style=flat

[license-url]: https://github.com/FreshMag/subjekt/blob/master/LICENSE

[conventional-commits-shield]: https://img.shields.io/badge/Conventional%20Commits-1.0.0-%23FE5196?logo=conventionalcommits

[conventional-commits-url]: https://conventionalcommits.org
