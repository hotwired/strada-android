package dev.hotwire.strada

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageTest {
    @Test
    fun replacing() {
        val message = Message(
            id = "1",
            component = "page",
            event = "connect",
            jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
        )

        val newMessage = message.replacing(
            event = "disconnect",
            jsonData = "{}"
        )

        assertEquals("1", newMessage.id)
        assertEquals("page", newMessage.component)
        assertEquals("disconnect", newMessage.event)
        assertEquals("{}", newMessage.jsonData)
    }
}
