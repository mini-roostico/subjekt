/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.utils

/**
 * Utility class used for reporting and collecting compilation messages.
 */
sealed class MessageCollector {
    /**
     * Represents a message type.
     */
    enum class MessageType {
        /**
         * Info message.
         */
        INFO,

        /**
         * Warning message.
         */
        WARNING,

        /**
         * Error message.
         */
        ERROR,
    }

    /**
     * Utility class used to represent a reported position in the source code.
     */
    data class Position(
        /**
         * The line number.
         */
        val line: Int = -1,
        /**
         * The character position in the line.
         */
        val charPositionInLine: Int = -1,
    ) {
        override fun toString(): String {
            val line = if (line == -1) "" else "line $line"
            val char = if (charPositionInLine == -1) "" else ":$charPositionInLine"
            return "$line$char"
        }
    }

    /**
     * The list of messages collected.
     */
    abstract val messages: List<Message>

    /**
     * Flushes the collected messages.
     */
    abstract fun flushMessages()

    /**
     * Represents a collected message. It has a [type] and a string [message].
     */
    data class Message(
        val type: MessageType,
        val message: String,
    )

    /**
     * Main method to report a [message].
     */
    open fun report(message: Message) {
        report(message)
    }

    /**
     * Shows one [message] in the console.
     */
    fun showInConsole(message: Message) {
        when (message.type) {
            MessageType.INFO -> println("i: ${message.message}")
            MessageType.WARNING -> println("w: ${message.message}")
            MessageType.ERROR -> println("e: ${message.message}")
        }
    }

    /**
     * Shows the collected messages in the console.
     */
    fun showInConsole() {
        messages.forEach(::showInConsole)
    }

//    /**
//     * Creates a listener to be used for ANTLR lexer and parser.
//     */
//    private fun createListener(context: Context): BaseErrorListener =
//        object : BaseErrorListener() {
//            override fun syntaxError(
//                recognizer: Recognizer<*, *>,
//                offendingSymbol: Any?,
//                line: Int,
//                charPositionInLine: Int,
//                msg: String,
//                e: RecognitionException?,
//            ) {
//                error(msg, context, Position(line, charPositionInLine))
//            }
//        }

//    /**
//     * Adds a listener to the given ANTLR [lexer] and [parser].
//     */
//    fun setLexerAndParser(
//        lexer: Lexer,
//        parser: Parser,
//        context: Context,
//    ) {
//        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
//        lexer.addErrorListener(createListener(context))
//        parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
//        parser.addErrorListener(createListener(context))
//    }

    /**
     * Checks if there are any errors in the collected messages.
     */
    fun hasErrors(): Boolean = messages.any { it.type == MessageType.ERROR }

    /**
     * Simple message collector that stores the messages in a list and reports all the messages into the console by
     * default. It can be silenced by setting [showErrors] to true.
     */
    class SimpleCollector(
        private val showErrors: Boolean = true,
        private val showWarnings: Boolean = true,
        private val showInfos: Boolean = false,
    ) : MessageCollector() {
        override var messages = emptyList<Message>()
            private set

        override fun report(message: Message) {
            messages += message
            if (showErrors && message.type == MessageType.ERROR) showInConsole(message)
            if (showWarnings && message.type == MessageType.WARNING) showInConsole(message)
            if (showInfos && message.type == MessageType.INFO) showInConsole(message)
        }

        override fun flushMessages() {
            messages = emptyList()
        }
    }

    /**
     * Null message collector that does not store or show any message.
     */
    class NullCollector : MessageCollector() {
        override val messages = emptyList<Message>()

        override fun report(message: Message) {
            // do nothing
        }

        override fun flushMessages() {
            // do nothing
        }
    }
}
