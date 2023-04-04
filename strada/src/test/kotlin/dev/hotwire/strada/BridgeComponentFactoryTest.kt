package dev.hotwire.strada

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BridgeComponentFactoryTest {
    @Test
    fun createComponents() {
        val factories = listOf(
            BridgeComponentFactory("one", ::OneBridgeComponent),
            BridgeComponentFactory("two", ::TwoBridgeComponent)
        )

        val delegate = AppBridgeDelegate(
            destinationLocation = "https://37signals.com",
            componentFactories = factories
        )

        val componentOne = factories[0].create(delegate)
        assertEquals("one", componentOne.name)
        assertTrue(componentOne is OneBridgeComponent)

        val componentTwo = factories[1].create(delegate)
        assertEquals("two", componentTwo.name)
        assertTrue(componentTwo is TwoBridgeComponent)
    }

    private class AppBridgeDelegate(
        destinationLocation: String,
        lifecycleOwner: LifecycleOwner = TestLifecycleOwner(),
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
