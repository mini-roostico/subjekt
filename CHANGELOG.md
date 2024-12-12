
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

