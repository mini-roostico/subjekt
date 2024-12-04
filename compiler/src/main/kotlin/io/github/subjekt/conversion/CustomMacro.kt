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
      val isVarArgs = method.isVarArgs

      if (paramTypes.dropLast(if (isVarArgs) 1 else 0).any { it != String::class.java }) {
        messageCollector.warning(
          "Skipping method '${method.name}' as it does not accept only String arguments",
          context,
          -1,
        )
        return null
      } else if (isVarArgs && paramTypes.last() != Array<String>::class.java) {
        messageCollector.warning(
          "Skipping method '${method.name}' as its vararg parameter is not of type String",
          context,
          -1,
        )
        return null
      } else if (method.returnType != List::class.java) {
        messageCollector.warning(
          "Skipping method '${method.name}' as it does not return a List<String>",
          context,
          -1,
        )
        return null
      }

      return object : CustomMacro {
        override val id: String = method.getAnnotation(Macro::class.java).name
        override val numberOfArguments: Int
          get() = if (isVarArgs) -1 else method.parameterTypes.size // -1 denotes varargs

        override fun eval(args: List<String>, messageCollector: MessageCollector): List<String> {
          try {
            val result: List<*> = if (isVarArgs) {
              val fixedArgs = paramTypes.dropLast(1).mapIndexed { index, _ -> args[index] }.toTypedArray()
              val varArgArray = args.drop(fixedArgs.size).toTypedArray()
              method.invoke(null, *fixedArgs, varArgArray) as List<*>
            } else {
              method.invoke(null, *args.toTypedArray()) as List<*>
            }
            return result.map { it.toString() }
          } catch (e: Exception) {
            messageCollector.error("Failed to invoke method '${method.name}': ${e.message}", context, -1)
            return emptyList()
          }
        }
      }
    }
  }
}
