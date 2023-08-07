package dev.hotwire.strada

abstract class BridgeComponent<in D : BridgeDestination>(
    val name: String,
    private val delegate: BridgeDelegate<D>
) {
    abstract fun onReceive(message: Message)

    fun replyWith(message: Message) {
        delegate.bridge?.replyWith(message) ?: run {
            logEvent("bridgeMessageFailedToReply", "bridge is not available")
        }
    }

    open fun onStart() {}
    open fun onStop() {}
}
