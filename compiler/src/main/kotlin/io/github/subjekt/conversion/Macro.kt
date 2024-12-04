package io.github.subjekt.conversion

/**
 * Annotation used to mark a Kotlin static method as a macro.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Macro(val name: String)
