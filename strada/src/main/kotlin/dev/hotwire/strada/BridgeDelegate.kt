package dev.hotwire.strada

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

abstract class BridgeDelegate(
    private val destinationLocation: String,
    private val lifecycleOwner: LifecycleOwner,
    private val componentFactories: List<BridgeComponentFactory<*, *>>
) {
    internal var bridge: Bridge? = null
    private var destinationIsActive = true

    private val components = mutableListOf<BridgeComponent>()
    protected val activeComponents: List<BridgeComponent>
        get() = when (destinationIsActive) {
            true -> components
            else -> emptyList()
        }

    init {
        observeLifeCycle()

        // TODO Components should be lazily created when the first message from the bridge is received.
        // componentFactories.forEach {
        //     components.add(it.create(this))
        // }
    }

    /**
     * Specifies whether the underlying WebView has cold booted to
     * a url (i.e. onPageFinished() called) and is ready for use.
     */
    abstract fun webViewIsReady(): Boolean

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
        bridge?.register(componentFactories.joinToString(" ") { it.name })
    }

    internal fun bridgeDidReceiveMessage(message: Message) {
        if (destinationLocation == message.metadata.url) {
            logMessage("bridgeDidReceiveMessage", message)
            component(message.component)?.handle(message)
        } else {
            logMessage("bridgeDidIgnoreMessage", message)
        }
    }

    private fun shouldReloadBridge(): Boolean {
        return webViewIsReady() && bridge?.isReady() == false
    }

    // Lifecycle events

    private fun observeLifeCycle() {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
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

    fun <T : BridgeComponent> component(clazz: Class<T>): T? {
        return activeComponents.filterIsInstance(clazz).firstOrNull()
    }

    private fun component(name: String): BridgeComponent? {
        return activeComponents.firstOrNull { it.name == name }
    }
}
