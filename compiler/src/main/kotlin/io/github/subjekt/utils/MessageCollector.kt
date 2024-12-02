package io.github.subjekt.utils

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.ConsoleErrorListener
import org.antlr.v4.runtime.Lexer
import org.antlr.v4.runtime.Parser
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

sealed class MessageCollector {

  enum class MessageType {
    INFO,
    WARNING,
    ERROR,
  }

  abstract val messages: List<Message>

  abstract fun info(message: String)
  abstract fun warning(message: String)
  abstract fun error(message: String)
  abstract fun flushMessages()

  data class Message(val type: MessageType, val message: String)

  fun showInConsole() {
    messages.forEach {
      when (it.type) {
        MessageType.INFO -> println("i: ${it.message}")
        MessageType.WARNING -> println("w: ${it.message}")
        MessageType.ERROR -> System.err.println("e: ${it.message}")
      }
    }
  }

  private val listener: BaseErrorListener by lazy {
    object : BaseErrorListener() {
      override fun syntaxError(
        recognizer: Recognizer<*, *>?,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String?,
        e: RecognitionException?,
      ) {
        error("Line $line:$charPositionInLine $msg")
      }
    }
  }

  fun useLexer(lexer: Lexer) {
    lexer.removeErrorListener(ConsoleErrorListener.INSTANCE)
    lexer.addErrorListener(listener)
  }

  fun useParser(parser: Parser) {
    parser.removeErrorListener(ConsoleErrorListener.INSTANCE)
    parser.addErrorListener(listener)
  }

  class SimpleCollector : MessageCollector() {
    override var messages = emptyList<Message>()
      private set

    override fun info(message: String) {
      messages += Message(MessageType.INFO, message)
    }

    override fun warning(message: String) {
      messages += Message(MessageType.WARNING, message)
    }

    override fun error(message: String) {
      messages += Message(MessageType.ERROR, message)
    }

    override fun flushMessages() {
      messages = emptyList()
    }
  }

  class NullCollector : MessageCollector() {
    override val messages = emptyList<Message>()
    override fun info(message: String) {}
    override fun warning(message: String) {}
    override fun error(message: String) {}
    override fun flushMessages() {}
  }
}
