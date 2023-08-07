package dev.hotwire.strada

import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BridgeComponentTest {
    private lateinit var component: TestData.OneBridgeComponent
    private val delegate: BridgeDelegate<TestData.AppBridgeDestination> = mock()
    private val bridge: Bridge = mock()

    private val message = Message(
        id = "1",
        component = "one",
        event = "connect",
        metadata = Metadata("https://37signals.com"),
        jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
    )

    @Before
    fun setup() {
        component = TestData.OneBridgeComponent("one", delegate)
        whenever(delegate.bridge).thenReturn(bridge)
    }

    @Test
    fun didReceive() {
        assertNull(component.messageReceivedForPublic("connect"))

        component.didReceive(message)
        assertEquals(message, component.messageReceivedForPublic("connect"))
    }

    @Test
    fun didReceiveSavesLastMessage() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.didReceive(message)
        assertEquals(message, component.messageReceivedForPublic("connect"))

        component.didReceive(newMessage)
        assertEquals(newMessage, component.messageReceivedForPublic("connect"))
    }

    @Test
    fun replyWith() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.replyWithPublic(newMessage)
        verify(bridge).replyWith(eq(newMessage))
    }

    @Test
    fun replyTo() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.didReceive(message)
        component.replyToPublic("connect", newJsonData)
        verify(bridge).replyWith(eq(newMessage))
    }

    @Test
    fun replyToIgnoresNotReceived() {
        val newJsonData = """{"title":"Page-title"}"""

        component.replyToPublic("connect", newJsonData)
        verify(bridge, never()).replyWith(any())
    }
}
