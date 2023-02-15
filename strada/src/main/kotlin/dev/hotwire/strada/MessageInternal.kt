package dev.hotwire.strada

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class MessageInternal(
    @SerialName("id") val id: String,
    @SerialName("component") val component: String,
    @SerialName("event") val event: String,
    @SerialName("data") val data: JsonElement = Json.parseToJsonElement("{}")
) {
    fun toMessage() = Message(
        id = id,
        component = component,
        event = event,
        data = data.toJson()
    )

    companion object {
        fun fromMessage(message: Message) = MessageInternal(
            id = message.id,
            component = message.component,
            event = message.event,
            data = Json.parseToJsonElement(message.data)
        )

        fun fromJson(json: String?) = try {
            json?.let { Json.decodeFromString<MessageInternal>(it) }
        } catch (e: Exception) {
            log("Invalid message: $json")
            null
        }
    }
}
