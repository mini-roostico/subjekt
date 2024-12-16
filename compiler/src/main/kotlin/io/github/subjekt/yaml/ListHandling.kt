package io.github.subjekt.yaml

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.type.CollectionType

class GenericListDeserializer<T>(private val valueType: JavaType) : StdDeserializer<List<T>>(valueType) {
  override fun deserialize(parser: JsonParser, context: DeserializationContext): List<T> {
    val codec = parser.codec
    val node = codec.readTree<JsonNode>(parser)

    return if (node.isArray) {
      node.map { codec.treeToValue(it, valueType.contentType.rawClass) }
    } else {
      listOf(codec.treeToValue(node, valueType.contentType.rawClass))
    }.mapNotNull { it as? T }
  }
}

class ListHandlingModule : SimpleModule() {
  override fun setupModule(context: SetupContext) {
    context.addDeserializers(object : Deserializers.Base() {
      override fun findCollectionDeserializer(
        collectionType: CollectionType,
        config: DeserializationConfig,
        beanDesc: BeanDescription,
        typeDeserializerForContent: TypeDeserializer?,
        elementDeserializer: JsonDeserializer<*>?,
      ): JsonDeserializer<*>? {
        return if (collectionType.rawClass == List::class.java) {
          val contentType = collectionType.contentType
          GenericListDeserializer<Any>(config.typeFactory.constructCollectionType(List::class.java, contentType))
        } else {
          null
        }
      }
    })
  }
}
