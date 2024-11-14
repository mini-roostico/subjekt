package io.github.subjekt.rendering.engines

import io.github.subjekt.files.Macro
import io.github.subjekt.rendering.Engine
import io.github.subjekt.stdlib.Utility
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import java.io.StringWriter

class VelocityEngine : Engine {
  override fun renderMacroDeclaration(macro: Macro): List<String> = macro.values.map {
    "#macro(${macro.name} ${macro.accepts.joinToString(" $", prefix = "$")})$it#end"
  }

  private fun injectStandardLibrary(context: VelocityContext) {
    context.put("util", Utility.javaClass)
  }

  override fun render(templateString: String, parametersMap: Map<String, Any>): String {
    val velocity = VelocityEngine()
    velocity.init()
    val context = VelocityContext()
    injectStandardLibrary(context)
    parametersMap.forEach { (key, value) -> context.put(key, value) }

    val writer = StringWriter()
    velocity.evaluate(context, writer, "StringTemplate", templateString)
    return writer.toString()
  }

}
