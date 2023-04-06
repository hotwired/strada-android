package dev.hotwire.strada

abstract class BridgeComponent<in D : BridgeDestination>(
    val name: String,
    private val delegate: BridgeDelegate<D>
) {
    abstract fun handle(message: Message)

    fun send(message: Message) {
        delegate.bridge?.send(message) ?: run {
            logEvent("bridgeMessageFailedToSend", "bridge is not available")
        }
    }

    open fun onStart() {}
    open fun onStop() {}
}
