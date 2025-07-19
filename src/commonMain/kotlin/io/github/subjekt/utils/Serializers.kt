package io.github.subjekt.utils

import io.github.subjekt.core.value.BooleanValue
import io.github.subjekt.core.value.FloatValue
import io.github.subjekt.core.value.IntValue
import io.github.subjekt.core.value.ObjectValue
import io.github.subjekt.core.value.StringValue
import io.github.subjekt.core.value.Value
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object Serializers {
    @OptIn(InternalSerializationApi::class)
    fun valueSerializer(): KSerializer<Value> =
        object : KSerializer<Value> {
            override val descriptor: SerialDescriptor =
                kotlinx.serialization.descriptors.buildSerialDescriptor(
                    "Value",
                    kotlinx.serialization.descriptors.StructureKind.OBJECT,
                )

            override fun serialize(
                encoder: Encoder,
                value: Value,
            ) {
                when (value) {
                    is BooleanValue -> encoder.encodeBoolean(value.value)
                    is FloatValue -> encoder.encodeDouble(value.value)
                    is IntValue -> encoder.encodeInt(value.value)
                    is StringValue -> encoder.encodeString(value.value)
                    is ObjectValue -> TODO("Serializzazione ObjectValue non implementata")
                }
            }

            override fun deserialize(decoder: Decoder): Value {
                // TODO: Implement deserialization logic for remaining Value types
                return StringValue(decoder.decodeString())
            }
        }
}
