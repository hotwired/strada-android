package dev.hotwire.strada

import androidx.lifecycle.LifecycleOwner

interface BridgeDestination {
    fun destinationLocation(): String
    fun destinationLifecycleOwner(): LifecycleOwner
    fun webViewIsReady(): Boolean
}
