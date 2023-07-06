package dev.hotwire.strada

import androidx.lifecycle.LifecycleOwner

interface BridgeDestination {
    /**
     * If the BridgeDestination LifecycleOwner is a Fragment's ViewLifecycleOwner, make sure to
     * return null here when the Fragment.getView() == null.
     */
    fun bridgeDestinationLifecycleOwner(): LifecycleOwner?
    fun bridgeDestinationLocation(): String
    fun bridgeWebViewIsReady(): Boolean
}
