package dev.hotwire.strada

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class BridgeDelegateTest {
    private lateinit var delegate: BridgeDelegate<AppBridgeDestination>
    private val bridge: Bridge = mock()

    private val factories = listOf(
        BridgeComponentFactory("one", ::OneBridgeComponent),
        BridgeComponentFactory("two", ::TwoBridgeComponent)
    )

    @Before
    fun setup() {
        delegate = BridgeDelegate(
            destination = AppBridgeDestination(),
            componentFactories = factories
        )
    }

    @Test
    fun loadBridgeInWebView() {
        delegate.onWebViewAttached(bridge)
        delegate.loadBridgeInWebView()
        verify(bridge, times(2)).load()
    }

    @Test
    fun resetBridge() {
        delegate.onWebViewAttached(bridge)
        delegate.resetBridge()
        verify(bridge).reset()
    }

    @Test
    fun bridgeDidInitialize() {
        delegate.onWebViewAttached(bridge)
        delegate.bridgeDidInitialize()
        verify(bridge).register(eq(listOf("one", "two")))
    }

    @Test
    fun bridgeDidReceiveMessage() {
        val message = Message(
            id = "1",
            component = "one",
            event = "connect",
            metadata = Metadata("https://37signals.com"),
            jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
        )

        assertNull(delegate.component<OneBridgeComponent>())
        assertEquals(true, delegate.bridgeDidReceiveMessage(message))
        assertNotNull(delegate.component<OneBridgeComponent>())
    }

    @Test
    fun bridgeDidReceiveMessageIgnored() {
        val message = Message(
            id = "1",
            component = "page",
            event = "connect",
            metadata = Metadata("https://37signals.com/another_url"),
            jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
        )

        assertEquals(false, delegate.bridgeDidReceiveMessage(message))
    }

    @Test
    fun onWebViewAttached() {
        whenever(bridge.isReady()).thenReturn(false)
        delegate.onWebViewAttached(bridge)

        assertEquals(delegate.bridge, bridge)
    }

    @Test
    fun onWebViewAttachedShouldLoad() {
        whenever(bridge.isReady()).thenReturn(false)
        delegate.onWebViewAttached(bridge)

        verify(bridge).load()
    }

    @Test
    fun onWebViewAttachedShouldNotLoad() {
        whenever(bridge.isReady()).thenReturn(true)
        delegate.onWebViewAttached(bridge)

        verify(bridge, never()).load()
    }

    @Test
    fun onWebViewDetached() {
        delegate.onWebViewAttached(bridge)
        delegate.onWebViewDetached()

        assertNull(delegate.bridge?.delegate)
        assertNull(delegate.bridge)
    }

    class AppBridgeDestination : BridgeDestination {
        override fun destinationLocation() = "https://37signals.com"
        override fun destinationLifecycleOwner() = TestLifecycleOwner(Lifecycle.State.STARTED)
        override fun webViewIsReady() = true
    }

    private abstract class AppBridgeComponent(
        name: String,
        delegate: BridgeDelegate<AppBridgeDestination>
    ) : BridgeComponent<AppBridgeDestination>(name, delegate)

    private class OneBridgeComponent(
        name: String,
        delegate: BridgeDelegate<AppBridgeDestination>
    ) : AppBridgeComponent(name, delegate) {
        override fun handle(message: Message) {}
    }

    private class TwoBridgeComponent(
        name: String,
        delegate: BridgeDelegate<AppBridgeDestination>
    ) : AppBridgeComponent(name, delegate) {
        override fun handle(message: Message) {}
    }
}
