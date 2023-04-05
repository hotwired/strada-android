package dev.hotwire.strada

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

abstract class BridgeDelegate<D : BridgeDestination>(
    val destination: D,
    private val componentFactories: List<BridgeComponentFactory<D, BridgeComponent<D>>>
) {
    internal var bridge: Bridge? = null
    private var destinationIsActive = true

    private val components = hashMapOf<String, BridgeComponent<D>>()
    protected val activeComponents: List<BridgeComponent<D>>
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

    fun onWebViewAttached(bridge: Bridge?) {
        this.bridge = bridge
        this.bridge?.delegate = this

        if (shouldReloadBridge()) {
            loadBridgeInWebView()
        }
    }

    fun onWebViewDetached() {
        bridge?.delegate = null
        bridge = null
    }

    internal fun bridgeDidInitialize() {
        bridge?.register(componentFactories.map { it.name })
    }

    internal fun bridgeDidReceiveMessage(message: Message) {
        if (destination.destinationLocation() == message.metadata?.url) {
            logMessage("bridgeDidReceiveMessage", message)
            getOrCreateComponent(message.component)?.handle(message)
        } else {
            logMessage("bridgeDidIgnoreMessage", message)
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

    // Retrieve an individual component

    fun <T : BridgeComponent<D>> component(clazz: Class<T>): T? {
        return activeComponents.filterIsInstance(clazz).firstOrNull()
    }

    private fun component(name: String): BridgeComponent<D>? {
        return activeComponents.firstOrNull { it.name == name }
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
