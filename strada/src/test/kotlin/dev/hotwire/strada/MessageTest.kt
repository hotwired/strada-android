package dev.hotwire.strada

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageTest {
    @Test
    fun replacing() {
        val metadata = Metadata("https://37signals.com")
        val message = Message(
            id = "1",
            component = "page",
            event = "connect",
            metadata = metadata,
            jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
        )

        val newMessage = message.replacing(
            event = "disconnect",
            jsonData = "{}"
        )

        assertEquals("1", newMessage.id)
        assertEquals("page", newMessage.component)
        assertEquals("disconnect", newMessage.event)
        assertEquals(metadata, newMessage.metadata)
        assertEquals("{}", newMessage.jsonData)
    }
}
