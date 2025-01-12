package io.github.subjekt.compiler.conversion

/**
 * Annotation used to mark a Kotlin object as a Subjekt module.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubjektModule(val name: String)
