package io.github.subjekt.conversion

import io.github.subjekt.nodes.Context
import io.github.subjekt.utils.MessageCollector
import java.lang.reflect.Method

interface CustomMacro {
  val id: String
  val numberOfArguments: Int

  fun eval(args: List<String>, messageCollector: MessageCollector): List<String>

  companion object {
    fun fromKotlinStatic(method: Method, context: Context, messageCollector: MessageCollector): CustomMacro? {
      val paramTypes = method.parameterTypes
      if (paramTypes.any { it != String::class.java }) {
        messageCollector.warning(
          "Skipping method '${method.name}' as it does not accept only String arguments",
          context,
          -1,
        )
        return null
      }
      return object : CustomMacro {
        override val id: String = method.name
        override val numberOfArguments: Int
          get() = method.parameterTypes.size

        override fun eval(args: List<String>, messageCollector: MessageCollector): List<String> {
          try {
            println(args)
            val result = method.invoke(null, *args.toTypedArray()) as List<String>
            return result
          } catch (e: Exception) {
            messageCollector.error("Failed to invoke method '${method.name}': ${e.message}", context, -1)
            return emptyList()
          }
        }
      }
    }
  }
}
