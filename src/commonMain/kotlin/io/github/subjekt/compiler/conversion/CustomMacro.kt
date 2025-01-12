/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.conversion

import io.github.subjekt.compiler.utils.MessageCollector

/**
 * A custom macro that can be used in Subjekt expressions.
 */
interface CustomMacro {
    /**
     * The unique identifier of the macro. This is the name that will be used in Subjekt expressions.
     */
    val id: String

    /**
     * The number of arguments that the macro accepts. If this is -1, the macro accepts a variable number of arguments.
     */
    val numberOfArguments: Int

    /**
     * Evaluates the macro with the given [args] and collecting messages with [messageCollector].
     */
    fun eval(
        args: List<String>,
        messageCollector: MessageCollector,
    ): String

    // TODO: switch to a Kotlin multiplatform way of creating custom macros
//    companion object {
//        /**
//         * Creates a [CustomMacro] from a Kotlin static method. Returns `null` if the method does not meet the requirements.
//         */
//        fun fromKotlinStatic(
//            method: Method,
//            context: Context,
//            messageCollector: MessageCollector,
//        ): CustomMacro? {
//            val paramTypes = method.parameterTypes
//            val isVarArgs = method.isVarArgs
//
//            if (paramTypes.dropLast(if (isVarArgs) 1 else 0).any { it != String::class.java }) {
//                messageCollector.warning(
//                    "Skipping method '${method.name}' as it does not accept only String arguments",
//                    context,
//                    -1,
//                )
//                return null
//            } else if (isVarArgs && paramTypes.last() != Array<String>::class.java) {
//                messageCollector.warning(
//                    "Skipping method '${method.name}' as its vararg parameter is not of type String",
//                    context,
//                    -1,
//                )
//                return null
//            } else if (method.returnType != String::class.java) {
//                messageCollector.warning(
//                    "Skipping method '${method.name}' as it does not return a List<String>",
//                    context,
//                    -1,
//                )
//                return null
//            }
//
//            return object : CustomMacro {
//                override val id: String = method.getAnnotation(Macro::class.java).name
//                override val numberOfArguments: Int
//                    get() = if (isVarArgs) -1 else method.parameterTypes.size // -1 denotes varargs
//
//                override fun eval(
//                    args: List<String>,
//                    messageCollector: MessageCollector,
//                ): String {
//                    try {
//                        val result: String =
//                            if (isVarArgs) {
//                                val fixedArgs =
//                                    paramTypes
//                                        .dropLast(1)
//                                        .mapIndexed {
//                                            index,
//                                            _,
//                                            ->
//                                            args[index]
//                                        }.toTypedArray()
//                                val varArgArray = args.drop(fixedArgs.size).toTypedArray()
//                                method.invoke(null, *fixedArgs, varArgArray) as String
//                            } else {
//                                method.invoke(null, *args.toTypedArray()) as String
//                            }
//                        return result
//                    } catch (e: Exception) {
//                        messageCollector.error("Failed to invoke method '${method.name}': ${e.message}", context, -1)
//                        return ""
//                    }
//                }
//            }
//        }
//    }
}
