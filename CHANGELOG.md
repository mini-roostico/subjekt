## [2.0.0](https://github.com/mini-roostico/subjekt/compare/v1.1.4...2.0.0) (2025-01-17)

### ⚠ BREAKING CHANGES

* submodules are no longer available!

### Features

* added base classes for new `core` package ([2de3dcd](https://github.com/mini-roostico/subjekt/commit/2de3dcdfa55e9f3f02e258a0ffc5bb3c15435760))
* added Configuration.kt ([971b5fa](https://github.com/mini-roostico/subjekt/commit/971b5fa4303c415f6a64e9b74ca0137fd003c47e))
* added equal and hashCode for Resolvable.kt and made internal classes data classes ([89e7c6c](https://github.com/mini-roostico/subjekt/commit/89e7c6c3be6247e50182cbd38ca0ece55ccdc789))
* added file IO utilities ([0c4078d](https://github.com/mini-roostico/subjekt/commit/0c4078dafec9f2e57e59f10146d365663253e1c4))
* added method 'toString' for Configuration ([8160a81](https://github.com/mini-roostico/subjekt/commit/8160a816d743fbb9c65e043f25da0fc8a285813d))
* added Resolvables to MapVisitor.kt and Subject.kt ([eb67e20](https://github.com/mini-roostico/subjekt/commit/eb67e2013791cd2b4d1507ad57fe0faf70da6a39))
* added SubjectBuilder to SuiteFactory.kt ([59d8b4a](https://github.com/mini-roostico/subjekt/commit/59d8b4ae4dd3af7b4f0cfe75425febaf1828f14d))
* added SymbolTable to SuiteFactory.kt and Parameter and Macros parsing ([61f6aad](https://github.com/mini-roostico/subjekt/commit/61f6aad2b3b28d29c077de8c53e5229262504727))
* added the `format` method and the relative spec ([a9c0acb](https://github.com/mini-roostico/subjekt/commit/a9c0acb47fea331a4d7bc555d797ce57296f0014))
* added utility getter for Subject's name and change default field to 'name' ([0032c43](https://github.com/mini-roostico/subjekt/commit/0032c43c47eb98e693542bba0bd7d8c531462287))
* added utility methods in SymbolTable.kt ([c00efd4](https://github.com/mini-roostico/subjekt/commit/c00efd41ecbed7454d7982fa934b7b0fbb491885))
* added utility to get a snapshot of the Configuration built inside a SuiteBuilder ([883b82b](https://github.com/mini-roostico/subjekt/commit/883b82bbd40f18385849f581e5b8cf17738701ad))
* create the Parameter class ([c55201d](https://github.com/mini-roostico/subjekt/commit/c55201d333f9dd6d2215713d265d7618cb39af0a))
* created the Macro class ([b3e3e73](https://github.com/mini-roostico/subjekt/commit/b3e3e735b0286380e0906b48eabf4f55366df5bc))
* first implementation of the Resolvable class ([c6c21cc](https://github.com/mini-roostico/subjekt/commit/c6c21cc4f0a77964c35bb01b0226c1a88d562a97))
* implemented 'toString' for Resolvable ([f691029](https://github.com/mini-roostico/subjekt/commit/f6910290849e89305237805185caae20b57282d1))
* implemented equal and hashCode for core classes ([c5dadd3](https://github.com/mini-roostico/subjekt/commit/c5dadd3651c0381cb60d36518fb7b23851993dab))
* implemented SymbolTable.kt ([67ee893](https://github.com/mini-roostico/subjekt/commit/67ee89321bb7b33b0eebbb8608c8bdd59837910c))
* moved files utilities to jvm only module. Added tests for sources ([9dc40a0](https://github.com/mini-roostico/subjekt/commit/9dc40a085168b9bbc2dfa8263aea6aeec03860ae))
* restructured all the project switching from monorepo to multi-repo strategy ([bf3e38e](https://github.com/mini-roostico/subjekt/commit/bf3e38eade90e5f1e2c06f5fec73f711931bc4c4))
* started skeleton for SuiteFactory.kt and Suite building logic. ([5eeb175](https://github.com/mini-roostico/subjekt/commit/5eeb175e77bb1fb7d53e7b9c4945c32f2286c601))
* started working on Subject parsing ([ed3a006](https://github.com/mini-roostico/subjekt/commit/ed3a006bea91f327565e20cacd6733e41a0b0563))
* started working on the Configuration part of the Suite creation ([8937001](https://github.com/mini-roostico/subjekt/commit/89370012896f04428ebfd20b347afe838e2f7dba))
* started working on the Subjekt parsing part ([46b8cda](https://github.com/mini-roostico/subjekt/commit/46b8cda7c8be164cad83e8b5b65f663073e6df46))

### Bug Fixes

* added missing id during subject construction ([ac9f25a](https://github.com/mini-roostico/subjekt/commit/ac9f25afc5685b1ad4115ef75be109738f22765f))
* clashing declaration ([3179289](https://github.com/mini-roostico/subjekt/commit/3179289944125622f5da44f3e91c1c30ee9e29a6))
* fixed bad subject creation not setting the expression delimiters correctly ([f7e96c7](https://github.com/mini-roostico/subjekt/commit/f7e96c7c40e98d7526c35d1acb19f0b652c86cbc))
* fixed checking errors (style, miscellaneous bugs). Checking still not passing ([256bfe8](https://github.com/mini-roostico/subjekt/commit/256bfe81645041702ffb6a81750d607374229b7d))
* fixed Configuration not handling YAML booleans correctly ([25298e1](https://github.com/mini-roostico/subjekt/commit/25298e11711adfcb4827690c37acb398497689ba))
* fixed configurationSnapshot not returned correctly ([58ae038](https://github.com/mini-roostico/subjekt/commit/58ae038bf516d28dd10ef01691f05f7f038355b1))
* fixed expression collapsing inside Resolvable.kt, added toFormattableString method ([f3940e2](https://github.com/mini-roostico/subjekt/commit/f3940e283e2cdb6c3c7be722a5e6e867092908de))
* fixed single macro as string throwing exception ([66994de](https://github.com/mini-roostico/subjekt/commit/66994de34afef47a351b76c1a2fa446a4591425d))
* RegExp bugs on Node and Browser JS ([cc93d67](https://github.com/mini-roostico/subjekt/commit/cc93d67ec0c60ad22dd4c84df274735390075e01))
* temporarily removed error inside SuiteVisitor.kt ([d87f8e8](https://github.com/mini-roostico/subjekt/commit/d87f8e876b2d2c7de6be4f0f72565534362f6fd0))
* temporarily removed errors inside the main module ([4b7f31c](https://github.com/mini-roostico/subjekt/commit/4b7f31c089773d80e8c2facd4ed08779ca9e3ba5))
* temporarily removed TODOs ([88225ac](https://github.com/mini-roostico/subjekt/commit/88225ac8ee8840ee2ec88f8ddb11c531e7e291f1))

### Tests

* added more clear print on test fail ([f4ddbcd](https://github.com/mini-roostico/subjekt/commit/f4ddbcd130e6ff04bcf101a5c212f1408ebf5635))
* added more tests with synonyms for Suite parsing ([e2f98e6](https://github.com/mini-roostico/subjekt/commit/e2f98e60080aedd1126caa52b63b5143cd581a9a))
* added ResolvableSpec.kt ([0fd1aea](https://github.com/mini-roostico/subjekt/commit/0fd1aeafd8fe68155b847f4af9dff4c5443b926e))
* added test for Macros ([a77a18c](https://github.com/mini-roostico/subjekt/commit/a77a18cf14628ace4953c0ddec59c7253b6a6077))
* added test in SuiteParsingSpec.kt ([c9b0170](https://github.com/mini-roostico/subjekt/commit/c9b0170962c20ed9d7359e250d11713278a81188))
* added testing case ([b457a12](https://github.com/mini-roostico/subjekt/commit/b457a12f9749ba1b06221972622f60e9a0fe6be2))
* added testing case in SuiteParsingSpec.kt ([dadceb8](https://github.com/mini-roostico/subjekt/commit/dadceb87b110746c84d1be0f20e2cf2ec2d4da18))
* added tests for parameters and macros inside SuiteParsingSpec.kt ([62fea40](https://github.com/mini-roostico/subjekt/commit/62fea40691dd4b56a307cfaf3c962995e58fffa2))
* added tests for parameters inside SuiteParsingSpec.kt ([b76eae7](https://github.com/mini-roostico/subjekt/commit/b76eae767f2e0a5b7005d8ded96ffa80b88c4a1f))
* extracted testing utilities. Created a new SuiteParsingSpec.kt ([4036d94](https://github.com/mini-roostico/subjekt/commit/4036d943a04c6a93abe06a93e6c23090af291164))
* moved files test to jvm module ([3c34fd3](https://github.com/mini-roostico/subjekt/commit/3c34fd3e639e480a8dffa0443fc6a74ad7e3acb5))
* re-enabled test ([04064f9](https://github.com/mini-roostico/subjekt/commit/04064f99125ec649f7699fcaf8ade3adb56fc918))
* temporarily disabled all tests ([2d53c89](https://github.com/mini-roostico/subjekt/commit/2d53c8941a067777296256de8a68d438469b0efa))

### Build and continuous integration

* added json dependency ([a9b5c79](https://github.com/mini-roostico/subjekt/commit/a9b5c792ac3092814489c80d6ebeacb686844c54))
* added kotlinx-io ([f3c0c04](https://github.com/mini-roostico/subjekt/commit/f3c0c0451a7ece04b5cccc76167d5f02b2e31640))
* added publication part inside build.gradle.kts ([4b66692](https://github.com/mini-roostico/subjekt/commit/4b666923662eb9dfcfebcb874dd5fef8e4799095))
* now commits runs 'check' task ([3684689](https://github.com/mini-roostico/subjekt/commit/368468975c89d08e959b94d125bccf36743116d5))
* temporarily disabled publishing ([598bf3e](https://github.com/mini-roostico/subjekt/commit/598bf3ec92624fc9d370c83325a6c696af4afcbf))
* updated npm package name ([826ff87](https://github.com/mini-roostico/subjekt/commit/826ff879d230db244db985dc7d4e17df9f23f2e9))
* updated versions ([db73c18](https://github.com/mini-roostico/subjekt/commit/db73c187796f8a17847e832df5fd8ba3dc7c2e51))
* updated versions ([db6145c](https://github.com/mini-roostico/subjekt/commit/db6145c332bca93d3c52220c1d9d54a6864770c9))

### General maintenance

* added copyright ([9f1bf3a](https://github.com/mini-roostico/subjekt/commit/9f1bf3a58d0ed258ea4ed2e0c9bc3fe22dde7566))
* optimized imports ([8ca4a6c](https://github.com/mini-roostico/subjekt/commit/8ca4a6cc96dc01b2f4783a425c49dfa1038d7e56))
* removed print ([e27b4e6](https://github.com/mini-roostico/subjekt/commit/e27b4e616c3d0298eb3391602ccefc5cc9ab6dbc))

### Style improvements

* fixed style in Configuration.kt ([d0cb2d2](https://github.com/mini-roostico/subjekt/commit/d0cb2d2a89548e6d11dd07a539d168081019d2c9))
* fixed style inside temporary classes ([8e27ea5](https://github.com/mini-roostico/subjekt/commit/8e27ea5db6ce69a8e8d82090e7adf88516f24d62))

### Refactoring

* added keys names for values ([c886b0b](https://github.com/mini-roostico/subjekt/commit/c886b0bc09cede64598b71b0e942a38892e15fc6))
* begin refactoring the MessageCollector.kt while maintaining old version ([8151c4e](https://github.com/mini-roostico/subjekt/commit/8151c4e3e713b5502455fd15dd90a509f23a5926))
* changed "fields" to "resolvables" ([edbb18c](https://github.com/mini-roostico/subjekt/commit/edbb18c00458139faf650fcc4046395ec724c402))
* converted Suite from interface to data class ([4a90a49](https://github.com/mini-roostico/subjekt/commit/4a90a491ce4c0ec3b7eb54e2237d58cf4ae79ece))
* extracted utilities to Utils.kt ([b6cb2a5](https://github.com/mini-roostico/subjekt/commit/b6cb2a5a656dc4f670d9aa7b3a3d2a8017f8904b))

# [1.1.4] - 2024-12-17
## Features
- YAML imports from other Subjekt YAMLs
- Flexible YAML arrays, supporting direct elements as single-element arrays
- Flexible YAML fields, supporting missing fields in the YAML
- Added resolvable, custom properties in subjects, customizable by the user


# [1.1.3] - 2024-12-12
- Updated compatibility with Java 8
- Added more generators in the API


# [1.1.2] - 2024-12-11
- General bug fixes
- Fixed Ktlint rule providers


# [1.1.1] - 2024-12-10
## Compiler
- Added better expression resolution
- Custom macros now work with single string result
- Single expression resolution to provide more consistent and predictable results

## API
- Added automatic linting to generated sources

## Gradle plugin
- Implemented a simple gradle plugin to use Subjekt through gradle


# [1.1.0] - 2024-12-05
Significant changes to the , , and  modules.

### Compiler Enhancements

- Developed the  object to compile YAML code, files, and resources into  instances.
- Added ANTLR grammar for parsing expressions, including macro calls and dot calls.

### DSL and Context Implementation

- Introduced the  object as the main entry point for the DSL, including a  and  function.
- New class to manage source files and directories for the DSL.
- Created the  class for handling YAML source files and generating subjects.

### Utility Functions and File Generation

- Added utility functions for file handling, such as .
- Implemented the  object to generate files from a .


# [1.0.0] - 2024-11-15
## Features
- Generation of Kotlin sources from YAML files
- Basic CLI to specify input and output paths
- Skeleton for test generation
