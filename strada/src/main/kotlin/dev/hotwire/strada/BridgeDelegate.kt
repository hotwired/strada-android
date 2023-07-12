package dev.hotwire.strada

import android.webkit.WebView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@Suppress("unused")
class BridgeDelegate<D : BridgeDestination>(
    val destination: D,
    private val componentFactories: List<BridgeComponentFactory<D, BridgeComponent<D>>>
) : DefaultLifecycleObserver {
    internal var bridge: Bridge? = null
    private var destinationIsActive: Boolean = false
    private val location: String = destination.bridgeDestinationLocation()
    private val initializedComponents = hashMapOf<String, BridgeComponent<D>>()

    val activeComponents: List<BridgeComponent<D>>
        get() = initializedComponents.map { it.value }.takeIf { destinationIsActive }.orEmpty()

    fun onColdBootPageCompleted() {
        bridge?.load()
    }

    fun onColdBootPageStarted() {
        bridge?.reset()
    }

    fun onWebViewAttached(webView: WebView) {
        bridge = Bridge.getBridgeFor(webView)?.apply {
            delegate = this@BridgeDelegate
        }

        if (bridge != null) {
            if (shouldReloadBridge()) {
                bridge?.load()
            }
        } else {
            logEvent("bridgeNotInitializedForWebView", location)
        }
    }

    fun onWebViewDetached() {
        bridge?.delegate = null
        bridge = null
    }

    internal fun bridgeDidInitialize() {
        bridge?.register(componentFactories.map { it.name })
    }

    internal fun bridgeDidReceiveMessage(message: Message): Boolean {
        return if (destinationIsActive && location == message.metadata?.url) {
            logMessage("bridgeDidReceiveMessage", message)
            getOrCreateComponent(message.component)?.handle(message)
            true
        } else {
            logMessage("bridgeDidIgnoreMessage", message)
            false
        }
    }

    private fun shouldReloadBridge(): Boolean {
        return destination.bridgeWebViewIsReady() && bridge?.isReady() == false
    }

    // Lifecycle events

    override fun onStart(owner: LifecycleOwner) {
        logEvent("bridgeDestinationDidStart", location)
        destinationIsActive = true
        activeComponents.forEach { it.onStart() }
    }

    override fun onStop(owner: LifecycleOwner) {
        activeComponents.forEach { it.onStop() }
        destinationIsActive = false
        logEvent("bridgeDestinationDidStop", location)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        destinationIsActive = false
        logEvent("bridgeDestinationDidDestroy", location)
    }

    // Retrieve component(s) by type

    inline fun <reified C> component(): C? {
        return activeComponents.filterIsInstance<C>().firstOrNull()
    }

    inline fun <reified C> forEachComponent(action: (C) -> Unit) {
        activeComponents.filterIsInstance<C>().forEach { action(it) }
    }

    private fun getOrCreateComponent(name: String): BridgeComponent<D>? {
        val factory = componentFactories.firstOrNull { it.name == name } ?: return null
        return initializedComponents.getOrPut(name) { factory.create(this) }
    }
}
