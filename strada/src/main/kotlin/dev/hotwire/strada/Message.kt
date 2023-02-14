package dev.hotwire.strada

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

typealias MessageData = JsonElement

inline fun <reified T> MessageData.decode(): T? = try {
    Json.decodeFromJsonElement<T>(this)
} catch (e: Exception) {
    null
}

@Serializable
data class Message(
    /**
     * A unique identifier for this message. You can reply to messages by sending
     * the same message back, or creating a new message with the same id
     */
    @SerialName("id") val id: String,

    /**
     * The component the message is sent from (e.g. - "form", "page", etc)
     */
    @SerialName("component") val component: String,

    /**
     * The event that this message is about: "submit", "display", "send"
     */
    @SerialName("event") val event: String,

    /**
     * Any data to send along with the message, for a "page" component,
     * this might be the ["title": "Page Title"]
     */
    @SerialName("data") val data: MessageData
) {
    fun toJSON(): String {
        return Json.encodeToString(this)
    }

    companion object {
        fun encodeData(vararg entries: Pair<String, String>): MessageData {
            return buildJsonObject {
                entries.forEach {
                    put(it.first, it.second)
                }
            }
        }

        inline fun <reified T> encodeData(value: T): MessageData {
            return Json.encodeToJsonElement(value)
        }

        fun fromJSON(json: String?): Message? = try {
            json?.let { Json.decodeFromString<Message>(it) }
        } catch (e: Exception) {
            log("Invalid message: $json")
            null
        }
    }
}
