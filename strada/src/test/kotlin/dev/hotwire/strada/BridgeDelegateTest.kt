package dev.hotwire.strada

import android.webkit.WebView
import androidx.core.view.get
import androidx.lifecycle.testing.TestLifecycleOwner
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class BridgeDelegateTest {
    private lateinit var delegate: BridgeDelegate<AppBridgeDestination>
    private val bridge: Bridge = mock()
    private val webView: WebView = mock()

    private val factories = listOf(
        BridgeComponentFactory("one", ::OneBridgeComponent),
        BridgeComponentFactory("two", ::TwoBridgeComponent)
    )

    @Before
    fun setup() {
        whenever(bridge.webView).thenReturn(webView)
        Bridge.initialize(bridge)

        delegate = BridgeDelegate(
            destination = AppBridgeDestination(),
            componentFactories = factories
        )
        delegate.bridge = bridge
    }

    @Test
    fun loadBridgeInWebView() {
        delegate.loadBridgeInWebView()
        verify(bridge).load()
    }

    @Test
    fun resetBridge() {
        delegate.resetBridge()
        verify(bridge).reset()
    }

    @Test
    fun bridgeDidInitialize() {
        delegate.bridgeDidInitialize()
        verify(bridge).register(eq(listOf("one", "two")))
    }

    @Test
    fun bridgeDidReceiveMessage() {
        val message = Message(
            id = "1",
            component = "page",
            event = "connect",
            metadata = Metadata("https://37signals.com"),
            jsonData = """{"title":"Page-title","subtitle":"Page-subtitle"}"""
        )

        assertEquals(true, delegate.bridgeDidReceiveMessage(message))
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
        delegate.onWebViewAttached(webView)

        assertEquals(delegate.bridge, bridge)
    }

    @Test
    fun onWebViewAttachedShouldLoad() {
        whenever(bridge.isReady()).thenReturn(false)
        delegate.onWebViewAttached(webView)

        verify(bridge).load()
    }

    @Test
    fun onWebViewAttachedShouldNotLoad() {
        whenever(bridge.isReady()).thenReturn(true)
        delegate.onWebViewAttached(webView)

        verify(bridge, never()).load()
    }

    @Test
    fun onWebViewDetached() {
        delegate.onWebViewDetached()

        assertNull(delegate.bridge?.delegate)
        assertNull(delegate.bridge)
    }

    class AppBridgeDestination : BridgeDestination {
        override fun destinationLocation() = "https://37signals.com"
        override fun destinationLifecycleOwner() = TestLifecycleOwner()
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
