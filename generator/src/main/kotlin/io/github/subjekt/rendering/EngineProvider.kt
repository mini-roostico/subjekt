package io.github.subjekt.rendering

object EngineProvider {

  private var engines: Map<String, Engine> = mapOf()
  private lateinit var selected: String

  fun register(name: String, engine: Engine) {
    engines += name to engine
    selected = name
  }

  fun inject(name: String = selected): Engine = engines.getValue(name)
}
