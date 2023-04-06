package dev.hotwire.strada

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

        val delegate = BridgeDelegate(
            destination = AppBridgeDestination(),
            componentFactories = factories
        )

        val componentOne = factories[0].create(delegate)
        assertEquals("one", componentOne.name)
        assertTrue(componentOne is OneBridgeComponent)

        val componentTwo = factories[1].create(delegate)
        assertEquals("two", componentTwo.name)
        assertTrue(componentTwo is TwoBridgeComponent)
    }

    class AppBridgeDestination : BridgeDestination {
        override fun bridgeDestinationLocation() = "https://37signals.com"
        override fun bridgeDestinationLifecycleOwner() = TestLifecycleOwner()
        override fun bridgeWebViewIsReady() = true
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
