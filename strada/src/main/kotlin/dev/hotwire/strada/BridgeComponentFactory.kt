package dev.hotwire.strada

class BridgeComponentFactory<in D : BridgeDelegate, out C : BridgeComponent> constructor(
    val name: String,
    private val creator: (name: String, delegate: D) -> C
) {
    fun create(delegate: D) = creator(name, delegate)
}
