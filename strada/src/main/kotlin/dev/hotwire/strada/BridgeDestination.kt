package dev.hotwire.strada

import androidx.lifecycle.LifecycleOwner

interface BridgeDestination {
    fun bridgeDestinationLocation(): String
    fun bridgeDestinationLifecycleOwner(): LifecycleOwner
    fun bridgeWebViewIsReady(): Boolean
}
