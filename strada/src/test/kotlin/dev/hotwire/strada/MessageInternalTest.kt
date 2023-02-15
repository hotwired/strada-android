package dev.hotwire.strada

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageInternalTest {
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
    fun toMessage() {
        val messageDataJson = """{"title":"Page-title","subtitle":"Page-subtitle","actions":["one","two","three"]}"""
        val message = MessageInternal(
            id = "1",
            component = "page",
            event = "connect",
            data = createPage().toJsonElement()
        ).toMessage()

        assertEquals("1", message.id)
        assertEquals("page", message.component)
        assertEquals("connect", message.event)
        assertEquals(messageDataJson, message.data)
    }

    @Test
    fun toJson() {
        val message = MessageInternal(
            id = "1",
            component = "page",
            event = "connect",
            data = createPage().toJsonElement()
        )

        assertEquals(json, message.toJson())
    }

    @Test
    fun fromJson() {
        val message = MessageInternal.fromJson(json)
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

    @Test
    fun fromJsonNoData() {
        val noDataJson = """{"id":"1","component":"page","event":"connect"}"""
        val message = MessageInternal.fromJson(noDataJson)

        assertEquals("1", message?.id)
        assertEquals(0, message?.data?.jsonObject?.size)
    }

    private fun createPage(): Page {
        return Page(
            title = "Page-title",
            subtitle = "Page-subtitle",
            actions = listOf("one", "two", "three")
        )
    }
}
