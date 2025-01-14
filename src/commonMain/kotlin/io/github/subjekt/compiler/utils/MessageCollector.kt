/*
 * Copyright (c) 2024, Francesco Magnani, Luca Rubboli,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 *  This file is part of Subjekt, and is distributed under the terms of the Apache License 2.0, as described in the
 *  LICENSE file in this project's repository's top directory.
 *
 */

package io.github.subjekt.compiler.utils

import io.github.subjekt.compiler.nodes.Context
import io.github.subjekt.utils.MessageCollector.Message
import io.github.subjekt.utils.MessageCollector.MessageType
import io.github.subjekt.utils.MessageCollector.Position
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.ConsoleErrorListener
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer

/**
 * Utility class used for reporting and collecting compilation messages.
 */
sealed class MessageCollector {
    /**
     * The list of messages collected.
     */
    abstract val messages: List<Message>

    /**
     * Reports a warning [message], in the given [position] within the given [context].
     */
    fun warning(
        message: String,
        context: Context,
        position: Position,
    ) {
        report(Message(MessageType.WARNING, message), context, position)
    }

    /**
     * Reports an info [message], referring to the given [line] within the given [context].
     */
    fun warning(
        message: String,
        context: Context,
        line: Int,
    ) {
        report(Message(MessageType.WARNING, message), context, line)
    }

    /**
     * Reports an error [message], in the given [position] within the given [context].
     */
    fun error(
        message: String,
        context: Context,
        position: Position,
    ) {
        report(Message(MessageType.ERROR, message), context, position)
    }

    /**
     * Reports an error [message], referring to the given [line] within the given [context].
     */
    fun error(
        message: String,
        context: Context,
        line: Int,
    ) {
        report(Message(MessageType.ERROR, message), context, line)
    }

    /**
     * Flushes the collected messages.
     */
    abstract fun flushMessages()

    /**
     * Main method to report a [message] in the given [context] at the given [line].
     */
    fun report(
        message: Message,
        context: Context,
        line: Int,
    ) {
        report(message, context, Position(line))
    }

    /**
     * Main method to report a [message] in the given [context] at the given [position].
     */
    abstract fun report(
        message: Message,
        context: Context,
        position: Position,
    )

    /**
     * Shows the collected messages in the console.
     */
    fun showInConsole(message: Message) {
        // do nothing
        println(message)
    }

    /**
     * Creates a listener to be used for ANTLR lexer and parser.
     */
    private fun createListener(context: Context): BaseErrorListener =
        object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String,
                e: RecognitionException?,
            ) {
                error(msg, context, Position(line, charPositionInLine))
            }
        }

    /**
     * Adds a listener to the given ANTLR [lexer] and [parser].
     */
    fun setLexerAndParser(
        lexer: Lexer,
        parser: Parser,
        context: Context,
    ) {
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
        lexer.addErrorListener(createListener(context))
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
        parser.addErrorListener(createListener(context))
    }

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

        private fun preprocessMessage(
            message: String,
            context: Context,
            position: Position,
        ): String {
            val suite = if (context.suiteName.isBlank()) "" else "Suite: '${context.suiteName}', "
            val subject = if (context.subjektName.isBlank()) "" else "Subject: '${context.subjektName}' "
            val position = if (position.toString().isBlank()) "" else "$position: "
            return "$suite$subject$position$message"
        }

        override fun report(
            message: Message,
            context: Context,
            position: Position,
        ) {
            val message = message.copy(message = preprocessMessage(message.message, context, position))
            messages += message
            if (showErrors && message.type == MessageType.ERROR) showInConsole(message)
            if (showWarnings && message.type == MessageType.WARNING) showInConsole(message)
            if (showInfos && message.type == MessageType.INFO) showInConsole(message)
        }

        override fun flushMessages() {
            messages = emptyList()
        }
    }
}
