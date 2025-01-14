/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.utils

import io.github.subjekt.utils.MessageCollector.Message

/**
 * Utility class used for logging messages.
 */
object Logger {
    /**
     * Whether to show messages in the console.
     */
    var showInConsole: Boolean = true

    /**
     * The message collector used to report warnings and errors.
     */
    val messageCollector: MessageCollector by lazy {
        MessageCollector.SimpleCollector(
            showInfos = showInConsole,
            showWarnings = showInConsole,
            showErrors = showInConsole,
        )
    }

    /**
     * Logs a warning message.
     */
    fun warning(
        preprocess: (String) -> String = { it },
        message: () -> String,
    ) {
        messageCollector.report(Message(MessageCollector.MessageType.WARNING, preprocess(message())))
    }

    /**
     * Logs an error message.
     */
    fun error(
        preprocess: (String) -> String = { it },
        message: () -> String,
    ) {
        messageCollector.report(Message(MessageCollector.MessageType.ERROR, preprocess(message())))
    }
}
