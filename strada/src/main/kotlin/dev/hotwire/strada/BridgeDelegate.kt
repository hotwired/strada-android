package dev.hotwire.strada

import android.webkit.WebView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

@Suppress("unused")
class BridgeDelegate<D : BridgeDestination>(
    val destination: D,
    private val componentFactories: List<BridgeComponentFactory<D, BridgeComponent<D>>>
) {
    internal var bridge: Bridge? = null
    private val initializedComponents = hashMapOf<String, BridgeComponent<D>>()
    private val lifecycle
        get() = destination.bridgeDestinationLifecycleOwner()?.lifecycle
    private val destinationIsActive
        get() = lifecycle?.currentState?.isAtLeast(Lifecycle.State.STARTED) ?: false

    private val allComponents: List<BridgeComponent<D>>
        get() = initializedComponents.map { it.value }

    val activeComponents: List<BridgeComponent<D>>
        get() = allComponents.takeIf { destinationIsActive } ?: emptyList()

    init {
        observeLifeCycle()
    }

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
            logEvent("bridgeNotInitializedForWebView", destination.bridgeDestinationLocation())
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
        return if (destinationIsActive && destination.bridgeDestinationLocation() == message.metadata?.url) {
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

    private fun observeLifeCycle() {
        destination.bridgeDestinationLifecycleOwner()?.lifecycle?.addObserver(object :
            DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                onStop()
            }
        })
    }

    private fun onStart() {
        activeComponents.forEach { it.onStart() }
    }

    private fun onStop() {
        allComponents.forEach { it.onStop() }
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
