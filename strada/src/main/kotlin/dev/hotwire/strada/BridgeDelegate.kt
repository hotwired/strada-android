package dev.hotwire.strada

import android.webkit.WebView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@Suppress("unused")
class BridgeDelegate<D : BridgeDestination>(
    val destination: D,
    private val componentFactories: List<BridgeComponentFactory<D, BridgeComponent<D>>>
) {
    internal var bridge: Bridge? = null
    private var destinationIsActive = true
    private val components = hashMapOf<String, BridgeComponent<D>>()

    val activeComponents: List<BridgeComponent<D>>
        get() = when (destinationIsActive) {
            true -> components.map { it.value }
            else -> emptyList()
        }

    init {
        observeLifeCycle()
    }

    fun loadBridgeInWebView() {
        bridge?.load()
    }

    fun resetBridge() {
        bridge?.reset()
    }

    fun onWebViewAttached(webView: WebView) {
        bridge = Bridge.getBridgeFor(webView)?.apply {
            delegate = this@BridgeDelegate
        }

        if (bridge != null) {
            if (shouldReloadBridge()) {
                loadBridgeInWebView()
            }
        } else {
            logEvent("bridgeNotInitializedForWebView", destination.destinationLocation())
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
        return if (destination.destinationLocation() == message.metadata?.url) {
            logMessage("bridgeDidReceiveMessage", message)
            getOrCreateComponent(message.component)?.handle(message)
            true
        } else {
            logMessage("bridgeDidIgnoreMessage", message)
            false
        }
    }

    private fun shouldReloadBridge(): Boolean {
        return destination.webViewIsReady() && bridge?.isReady() == false
    }

    // Lifecycle events

    private fun observeLifeCycle() {
        destination.destinationLifecycleOwner().lifecycle.addObserver(object :
            DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) { onStart() }
            override fun onStop(owner: LifecycleOwner) { onStop() }
        })
    }

    private fun onStart() {
        destinationIsActive = true
        activeComponents.forEach { it.onStart() }
    }

    private fun onStop() {
        destinationIsActive = false
        activeComponents.forEach { it.onStop() }
    }

    // Retrieve component(s) by type

    inline fun <reified C> component(): C? {
        return activeComponents.filterIsInstance<C>().firstOrNull()
    }

    inline fun <reified C> forEachComponent(action: (C) -> Unit) {
        activeComponents.filterIsInstance<C>().forEach { action(it) }
    }

    private fun getOrCreateComponent(name: String): BridgeComponent<D>? {
        components[name]?.let { return it }

        val factory = componentFactories.firstOrNull { it.name == name }

        return if (factory != null) {
            val component = factory.create(this)
            components[name] = component
            component
        } else {
            null
        }
    }
}
