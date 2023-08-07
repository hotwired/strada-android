package dev.hotwire.strada

object TestData {
    val componentFactories = listOf(
        BridgeComponentFactory("one", TestData::OneBridgeComponent),
        BridgeComponentFactory("two", TestData::TwoBridgeComponent)
    )

    val bridgeDelegate = BridgeDelegate(
        location = "https://37signals.com",
        destination = AppBridgeDestination(),
        componentFactories = componentFactories
    )

    class AppBridgeDestination : BridgeDestination {
        override fun bridgeWebViewIsReady() = true
    }

    abstract class AppBridgeComponent(
        name: String,
        delegate: BridgeDelegate<AppBridgeDestination>
    ) : BridgeComponent<AppBridgeDestination>(name, delegate)

    class OneBridgeComponent(
        name: String,
        delegate: BridgeDelegate<AppBridgeDestination>
    ) : AppBridgeComponent(name, delegate) {
        override fun onReceive(message: Message) {}

        fun replyWithPublic(message: Message) {
            replyWith(message)
        }

        fun replyToPublic(event: String, jsonData: String) {
            replyTo(event, jsonData)
        }

        fun messageReceivedForPublic(event: String): Message? {
            return messageReceivedFor(event)
        }
    }

    class TwoBridgeComponent(
        name: String,
        delegate: BridgeDelegate<AppBridgeDestination>
    ) : AppBridgeComponent(name, delegate) {
        override fun onReceive(message: Message) {}
    }
}