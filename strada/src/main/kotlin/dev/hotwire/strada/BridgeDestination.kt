package dev.hotwire.strada

interface BridgeDestination {
    fun bridgeDestinationLocation(): String
    fun bridgeWebViewIsReady(): Boolean
}
