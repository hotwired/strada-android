package dev.hotwire.strada

class BridgeComponentFactory<D : BridgeDestination, out C : BridgeComponent<D>> constructor(
    val name: String,
    private val creator: (name: String, delegate: BridgeDelegate<D>) -> C
) {
    fun create(delegate: BridgeDelegate<D>) = creator(name, delegate)
}
