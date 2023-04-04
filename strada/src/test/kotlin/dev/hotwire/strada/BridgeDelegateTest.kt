package dev.hotwire.strada

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class BridgeDelegateTest {
    private lateinit var delegate: BridgeDelegate
    private val bridge: Bridge = mock()

    private val factories = listOf(
        BridgeComponentFactory("one", ::OneBridgeComponent),
        BridgeComponentFactory("two", ::TwoBridgeComponent)
    )

    @Before
    fun setup() {
        delegate = AppBridgeDelegate(
            destinationLocation = "https://37signals.com",
            lifecycleOwner = TestLifecycleOwner(),
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
        verify(bridge).register(eq("one two"))
    }

    // TODO test bridgeDidReceiveMessage()

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

    private class AppBridgeDelegate(
        destinationLocation: String,
        lifecycleOwner: LifecycleOwner,
        componentFactories: List<BridgeComponentFactory<AppBridgeDelegate, AppBridgeComponent>>,
    ) : BridgeDelegate(destinationLocation, lifecycleOwner, componentFactories) {
        override fun webViewIsReady() = true
    }

    private abstract class AppBridgeComponent(
        name: String,
        delegate: AppBridgeDelegate
    ) : BridgeComponent(name, delegate)

    private class OneBridgeComponent(
        name: String,
        delegate: AppBridgeDelegate
    ) : AppBridgeComponent(name, delegate) {
        override fun handle(message: Message) {}
    }

    private class TwoBridgeComponent(
        name: String,
        delegate: AppBridgeDelegate
    ) : AppBridgeComponent(name, delegate) {
        override fun handle(message: Message) {}
    }
}
