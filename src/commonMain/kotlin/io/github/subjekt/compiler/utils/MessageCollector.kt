package io.github.subjekt.compiler.utils

import io.github.subjekt.compiler.nodes.Context
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.lang.System

/**
 * Utility class used for reporting and collecting compilation messages.
 */
sealed class MessageCollector {

  /**
   * Represents a message type.
   */
  enum class MessageType {
    INFO,
    WARNING,
    ERROR,
  }

  /**
   * Utility class used to represent a reported position in the source code.
   */
  data class Position(val line: Int = -1, val charPositionInLine: Int = -1) {
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
   * Reports an info [message], in the given [position] within the given [context]
   */
  fun info(message: String, context: Context, position: Position) {
    report(Message(MessageType.INFO, message), context, position)
  }

  /**
   * Reports an info [message], in the given [position] `(line, charInLine)` within the given [context]
   */
  fun info(message: String, context: Context, position: Pair<Int, Int>) {
    report(Message(MessageType.INFO, message), context, position)
  }

  /**
   * Reports an info [message], referring to the given [line] within the given [context]
   */
  fun info(message: String, context: Context, line: Int) {
    report(Message(MessageType.INFO, message), context, line)
  }

  /**
   * Reports a warning [message], in the given [position] within the given [context]
   */
  fun warning(message: String, context: Context, position: Position) {
    report(Message(MessageType.WARNING, message), context, position)
  }

  /**
   * Reports a warning [message], in the given [position] `(line, charInLine)` within the given [context]
   */
  fun warning(message: String, context: Context, position: Pair<Int, Int>) {
    report(Message(MessageType.WARNING, message), context, position)
  }

  /**
   * Reports an info [message], referring to the given [line] within the given [context]
   */
  fun warning(message: String, context: Context, line: Int) {
    report(Message(MessageType.WARNING, message), context, line)
  }

  /**
   * Reports an error [message], in the given [position] within the given [context]
   */
  fun error(message: String, context: Context, position: Position) {
    report(Message(MessageType.ERROR, message), context, position)
  }

  /**
   * Reports an error [message], in the given [position] `(line, charInLine)` within the given [context]
   */
  fun error(message: String, context: Context, position: Pair<Int, Int>) {
    report(Message(MessageType.ERROR, message), context, position)
  }

  /**
   * Reports an error [message], referring to the given [line] within the given [context]
   */
  fun error(message: String, context: Context, line: Int) {
    report(Message(MessageType.ERROR, message), context, line)
  }

  /**
   * Flushes the collected messages.
   */
  abstract fun flushMessages()

  /**
   * Represents a collected message. It has a [type] and a string [message].
   */
  data class Message(val type: MessageType, val message: String)

  /**
   * Main method to report a [message] in the given [context] at the given [position] `(line, charInLine)`.
   */
  fun report(message: Message, context: Context, position: Pair<Int, Int>) {
    report(message, context, Position(position.first, position.second))
  }

  /**
   * Main method to report a [message] in the given [context] at the given [line].
   */
  fun report(message: Message, context: Context, line: Int) {
    report(message, context, Position(line))
  }

  /**
   * Main method to report a [message] in the given [context] at the given [position].
   */
  abstract fun report(message: Message, context: Context, position: Position)

  /**
   * Shows one [message] in the console.
   */
  fun showInConsole(message: Message) {
    when (message.type) {
      MessageType.INFO -> println("i: ${message.message}")
      MessageType.WARNING -> println("w: ${message.message}")
      MessageType.ERROR -> System.err.println("e: ${message.message}")
    }
  }

  /**
   * Shows the collected messages in the console.
   */
  fun showInConsole() {
    messages.forEach(::showInConsole)
  }

  /**
   * Creates a listener to be used for ANTLR lexer and parser.
   */
  private fun createListener(context: Context): BaseErrorListener = object : BaseErrorListener() {
    override fun syntaxError(
      recognizer: Recognizer<*, *>?,
      offendingSymbol: Any?,
      line: Int,
      charPositionInLine: Int,
      msg: String?,
      e: RecognitionException?,
    ) {
      error(msg ?: "Error", context, Position(line, charPositionInLine))
    }
  }

  /**
   * Adds a listener to the given ANTLR [lexer].
   */
  fun useLexer(lexer: Lexer, context: Context) {
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
    lexer.addErrorListener(createListener(context))
  }

  /**
   * Adds a listener to the given ANTLR [parser].
   */
  fun useParser(parser: Parser, context: Context) {
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

    private fun preprocessMessage(message: String, context: Context, position: Position): String {
      val suite = if (context.suiteName.isBlank()) "" else "Suite: '${context.suiteName}', "
      val subject = if (context.subjektName.isBlank()) "" else "Subject: '${context.subjektName}' "
      val position = if (position.toString().isBlank()) "" else "$position: "
      return "$suite$subject$position$message"
    }

    override fun report(message: Message, context: Context, position: Position) {
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

  /**
   * Null message collector that does not store or show any message.
   */
  class NullCollector : MessageCollector() {
    override val messages = emptyList<Message>()
    override fun report(message: Message, context: Context, position: Position) {}
    override fun flushMessages() {}
  }
}
