package dev.hotwire.strada

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

internal inline fun <reified T> T.toJsonElement() = Json.encodeToJsonElement(this)

internal inline fun <reified T> T.toJson() = Json.encodeToString(this)

internal inline fun <reified T> JsonElement.decode(): T? = try {
    Json.decodeFromJsonElement<T>(this)
} catch (e: Exception) {
    null
}
