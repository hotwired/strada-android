package dev.hotwire.strada

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

        val componentOne = factories[0].create(AppBridgeDelegate(factories))
        assertEquals("one", componentOne.name)
        assertTrue(componentOne is OneBridgeComponent)

        val componentTwo = factories[1].create(AppBridgeDelegate(factories))
        assertEquals("two", componentTwo.name)
        assertTrue(componentTwo is TwoBridgeComponent)
    }

    private class AppBridgeDelegate(
        componentFactories: List<BridgeComponentFactory<AppBridgeDelegate, AppBridgeComponent>>,
    ) : BridgeDelegate(componentFactories) {
        override fun bridgeDidInitialize() {}
        override fun bridgeDidReceiveMessage(message: Message) {}
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
