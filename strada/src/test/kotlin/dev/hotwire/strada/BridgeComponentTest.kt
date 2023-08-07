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
    fun didStart() {
        assertEquals(false, component.onStartCalled)

        component.didStart()
        assertEquals(true, component.onStartCalled)
    }

    @Test
    fun didStop() {
        assertEquals(false, component.onStopCalled)

        component.didStop()
        assertEquals(true, component.onStopCalled)
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

        val replied = component.replyWithPublic(newMessage)
        assertEquals(true, replied)
        verify(bridge).replyWith(eq(newMessage))
    }

    @Test
    fun replyTo() {
        component.didReceive(message)

        val replied = component.replyToPublic("connect")
        assertEquals(true, replied)
        verify(bridge).replyWith(eq(message))
    }

    @Test
    fun replyToReplacingData() {
        val newJsonData = """{"title":"Page-title"}"""
        val newMessage = message.replacing(jsonData = newJsonData)

        component.didReceive(message)

        val replied = component.replyToPublic("connect", newJsonData)
        assertEquals(true, replied)
        verify(bridge).replyWith(eq(newMessage))
    }

    @Test
    fun replyToIgnoresNotReceived() {
        val replied = component.replyToPublic("connect")
        assertEquals(false, replied)
        verify(bridge, never()).replyWith(any())
    }

    @Test
    fun replyToReplacingDataIgnoresNotReceived() {
        val newJsonData = """{"title":"Page-title"}"""

        val replied = component.replyToPublic("connect", newJsonData)
        assertEquals(false, replied)
        verify(bridge, never()).replyWith(any())
    }
}
