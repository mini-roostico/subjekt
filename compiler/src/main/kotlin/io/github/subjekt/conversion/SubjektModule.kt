package io.github.subjekt.conversion

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SubjektModule(val name: String)
