package dev.hotwire.strada

interface BridgeDelegate {
    fun bridgeDidInitialize()
    fun bridgeDidReceiveMessage(message: Message)
}
