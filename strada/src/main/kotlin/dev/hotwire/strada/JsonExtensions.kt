package dev.hotwire.strada

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

internal fun String.parseToJsonElement() = json.parseToJsonElement(this)

internal inline fun <reified T> T.toJsonElement() = json.encodeToJsonElement(this)

internal inline fun <reified T> T.toJson() = json.encodeToString(this)

internal inline fun <reified T> JsonElement.decode(): T? = try {
    json.decodeFromJsonElement<T>(this)
} catch (e: Exception) {
    logError("jsonElementDecodeException", e)
    null
}

internal inline fun <reified T> String.decode(): T? = try {
    json.decodeFromString<T>(this)
} catch (e: Exception) {
    logError("jsonStringDecodeException", e)
    null
}

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
    isLenient = true
}
