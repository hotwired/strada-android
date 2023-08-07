package dev.hotwire.strada

abstract class BridgeComponent<in D : BridgeDestination>(
    val name: String,
    private val delegate: BridgeDelegate<D>
) {
    abstract fun onReceive(message: Message)

    fun replyTo(message: Message) {
        delegate.bridge?.replyTo(message) ?: run {
            logEvent("bridgeMessageFailedToReply", "bridge is not available")
        }
    }

    open fun onStart() {}
    open fun onStop() {}
}
