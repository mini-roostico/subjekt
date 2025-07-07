package io.github.subjekt.engine.expressions

/**
 * Exception thrown when a type cannot be resolved.
 *
 * @property message The detail message for the exception.
 * @property cause The cause of the exception, if any.
 */
class TypeException(
    /**
     * The detail message for the exception.
     */
    message: String,
    /**
     * The cause of the exception, if any.
     */
    cause: Throwable? = null,
) : Exception(message, cause)
