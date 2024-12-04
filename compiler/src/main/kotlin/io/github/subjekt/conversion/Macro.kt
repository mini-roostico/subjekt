package io.github.subjekt.conversion

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Macro(val name: String)
