package io.github.subjekt.core.value

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
class ObjectValue : Value(Type.OBJECT) {
    override fun cast(targetType: Type): Value {
        TODO("Not yet implemented")
    }

    override fun eq(other: Value): BooleanValue {
        TODO("Not yet implemented")
    }
}
