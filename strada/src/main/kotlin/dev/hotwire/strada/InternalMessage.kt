package dev.hotwire.strada

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class InternalMessage(
    @SerialName("id") val id: String,
    @SerialName("component") val component: String,
    @SerialName("event") val event: String,
    @SerialName("metadata") val metadata: InternalMetadata,
    @SerialName("data") val data: JsonElement = Json.parseToJsonElement("{}")
) {
    fun toMessage() = Message(
        id = id,
        component = component,
        event = event,
        metadata = Metadata(url = metadata.url),
        jsonData = data.toJson()
    )

    companion object {
        fun fromMessage(message: Message) = InternalMessage(
            id = message.id,
            component = message.component,
            event = message.event,
            metadata = InternalMetadata(url = message.metadata.url),
            data = Json.parseToJsonElement(message.jsonData)
        )

        fun fromJson(json: String?) = try {
            json?.let { Json.decodeFromString<InternalMessage>(it) }
        } catch (e: Exception) {
            logEvent("jsonDecodeException", "$json")
            null
        }
    }
}

@Serializable
internal data class InternalMetadata(
    @SerialName("url") val url: String
)
