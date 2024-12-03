package io.github.subjekt.utils

import io.github.subjekt.nodes.Context
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import java.lang.System

sealed class MessageCollector {

  enum class MessageType {
    INFO,
    WARNING,
    ERROR,
  }

  data class Position(val line: Int = -1, val charPositionInLine: Int = -1) {
    override fun toString(): String {
      val line = if (line == -1) "" else "line $line"
      val char = if (charPositionInLine == -1) "" else ":$charPositionInLine"
      return "$line$char"
    }
  }

  abstract val messages: List<Message>

  fun info(message: String, context: Context, position: Position) {
    report(Message(MessageType.INFO, message), context, position)
  }

  fun info(message: String, context: Context, position: Pair<Int, Int>) {
    report(Message(MessageType.INFO, message), context, position)
  }

  fun info(message: String, context: Context, line: Int) {
    report(Message(MessageType.INFO, message), context, line)
  }

  fun warning(message: String, context: Context, position: Position) {
    report(Message(MessageType.WARNING, message), context, position)
  }

  fun warning(message: String, context: Context, position: Pair<Int, Int>) {
    report(Message(MessageType.WARNING, message), context, position)
  }

  fun warning(message: String, context: Context, line: Int) {
    report(Message(MessageType.WARNING, message), context, line)
  }

  fun error(message: String, context: Context, position: Position) {
    report(Message(MessageType.ERROR, message), context, position)
  }

  fun error(message: String, context: Context, position: Pair<Int, Int>) {
    report(Message(MessageType.ERROR, message), context, position)
  }

  fun error(message: String, context: Context, line: Int) {
    report(Message(MessageType.ERROR, message), context, line)
  }

  abstract fun flushMessages()

  data class Message(val type: MessageType, val message: String)

  fun report(message: Message, context: Context, position: Pair<Int, Int>) {
    report(message, context, Position(position.first, position.second))
  }

  fun report(message: Message, context: Context, line: Int) {
    report(message, context, Position(line))
  }

  abstract fun report(message: Message, context: Context, position: Position)

  fun showInConsole(message: Message) {
    when (message.type) {
      MessageType.INFO -> println("i: ${message.message}")
      MessageType.WARNING -> println("w: ${message.message}")
      MessageType.ERROR -> System.err.println("e: ${message.message}")
    }
  }

  fun showInConsole() {
    messages.forEach(::showInConsole)
  }

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

  fun useLexer(lexer: Lexer, context: Context) {
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
    lexer.addErrorListener(createListener(context))
  }

  fun useParser(parser: Parser, context: Context) {
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
    parser.addErrorListener(createListener(context))
  }

  fun hasErrors(): Boolean = messages.any { it.type == MessageType.ERROR }

  class SimpleCollector(private val silent: Boolean = false) : MessageCollector() {
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
      if (!silent) showInConsole(message)
    }

    override fun flushMessages() {
      messages = emptyList()
    }
  }

  class NullCollector : MessageCollector() {
    override val messages = emptyList<Message>()
    override fun report(message: Message, context: Context, position: Position) {}
    override fun flushMessages() {}
  }
}
