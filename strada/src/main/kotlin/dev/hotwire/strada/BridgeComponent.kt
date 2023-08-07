package dev.hotwire.strada

abstract class BridgeComponent<in D : BridgeDestination>(
    val name: String,
    private val delegate: BridgeDelegate<D>
) {
    private val receivedMessages = hashMapOf<String, Message>()

    internal fun didReceive(message: Message) {
        receivedMessages[message.event] = message
        onReceive(message)
    }

    open fun onStart() {}
    open fun onStop() {}

    /**
     * Called when a message is received from the web bridge. Handle the
     * message for its `event` type for the custom component's behavior.
     */
    protected abstract fun onReceive(message: Message)

    /**
     * Reply to the web with a received message, optionally replacing its
     * `event` or `jsonData`.
     */
    protected fun replyWith(message: Message): Boolean {
        return reply(message)
    }

    /**
     * Reply to the web with the last received message for a given `event`
     * with its original `jsonData`.
     *
     * NOTE: If a message has not been received for the given `event`, the
     * reply will be ignored.
     */
    protected fun replyTo(event: String): Boolean {
        val message = messageReceivedFor(event) ?: run {
            logEvent("bridgeMessageFailedToReply", "message for event '$event' was not received")
            return false
        }

        return reply(message)
    }

    /**
     * Reply to the web with the last received message for a given `event`,
     * replacing its `jsonData`.
     *
     * NOTE: If a message has not been received for the given `event`, the
     * reply will be ignored.
     */
    protected fun replyTo(event: String, jsonData: String): Boolean {
        val message = messageReceivedFor(event) ?: run {
            logEvent("bridgeMessageFailedToReply", "message for event '$event' was not received")
            return true
        }

        return reply(message.replacing(jsonData = jsonData))
    }

    /**
     * Returns the last received message for a given `event`, if available.
     */
    protected fun messageReceivedFor(event: String): Message? {
        return receivedMessages[event]
    }

    private fun reply(message: Message): Boolean {
        delegate.bridge?.replyWith(message) ?: run {
            logEvent("bridgeMessageFailedToReply", "bridge is not available")
            return false
        }

        return true
    }
}
