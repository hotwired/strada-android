package dev.hotwire.strada

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageTest {
    @Serializable
    private data class Page(
        @SerialName("title") val title: String,
        @SerialName("subtitle") val subtitle: String,
        @SerialName("actions") val actions: List<String>
    )

    private val json = """{
        "id":"1",
        "component":"page",
        "event":"connect",
        "data":{
            "title":"Page-title",
            "subtitle":"Page-subtitle",
            "actions": [
                "one",
                "two",
                "three"
            ]
        }
    }""".replace("\\s".toRegex(), "")

    @Test
    fun messageDataObject() {
        val page = createPage()
        val data = Message.encodeData(page)
        val decodedPage = data.decode<Page>()

        assertEquals(3, data.jsonObject.size)
        assertEquals(page, decodedPage)
    }

    @Test
    fun messageDataPairs() {
        val data = Message.encodeData(
            "one-key" to "one-value",
            "two-key" to "two-value"
        )

        assertEquals(2, data.jsonObject.size)
        assertEquals("one-value", data.jsonObject["one-key"]?.jsonPrimitive?.content)
        assertEquals("two-value", data.jsonObject["two-key"]?.jsonPrimitive?.content)
    }

    @Test
    fun toJson() {
        val message = Message(
            id = "1",
            component = "page",
            event = "connect",
            data = Message.encodeData(createPage())
        )

        assertEquals(json, message.toJson())
    }

    @Test
    fun fromJson() {
        val message = Message.fromJson(json)
        val page = message?.data?.decode<Page>()

        assertEquals("1", message?.id)
        assertEquals("page", message?.component)
        assertEquals("connect", message?.event)
        assertEquals("Page-title", page?.title)
        assertEquals("Page-subtitle", page?.subtitle)
        assertEquals("one", page?.actions?.get(0))
        assertEquals("two", page?.actions?.get(1))
        assertEquals("three", page?.actions?.get(2))
    }

    private fun createPage(): Page {
        return Page(
            title = "Page-title",
            subtitle = "Page-subtitle",
            actions = listOf("one", "two", "three")
        )
    }
}
