package dev.hotwire.strada

abstract class BridgeDelegate(
    componentFactories: List<BridgeComponentFactory<*, *>>
) {
    abstract fun bridgeDidInitialize()
    abstract fun bridgeDidReceiveMessage(message: Message)
}
