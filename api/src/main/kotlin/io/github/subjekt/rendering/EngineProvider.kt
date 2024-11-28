package io.github.subjekt.rendering

import io.github.subjekt.rendering.engines.VelocityEngine

object EngineProvider {

  private var engines: Map<String, Engine> = mapOf()
  private var selected: String = "velocity"

  init {
      register("velocity", VelocityEngine())
  }

  fun register(name: String, engine: Engine) {
    engines += name to engine
    selected = name
  }

  fun inject(name: String = selected): Engine = engines.getValue(name)
}
