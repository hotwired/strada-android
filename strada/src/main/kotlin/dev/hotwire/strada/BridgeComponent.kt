package dev.hotwire.strada

abstract class BridgeComponent(
    val name: String,
    private val delegate: BridgeDelegate
) {
    abstract fun handle(message: Message)
    fun onStart() {}
    fun onStop() {}
}
