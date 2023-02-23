package dev.hotwire.strada

class BridgeComponentFactory<in D : BridgeDelegate, out C : BridgeComponent> internal constructor(
    val name: String,
    private val creator: (name: String, delegate: D) -> C
) {
    fun create(delegate: D) = creator(name, delegate)
}

fun <D : BridgeDelegate, C : BridgeComponent> bridgeComponentFactory(
    name: String,
    creator: (name: String, delegate: D) -> C
) = BridgeComponentFactory(name, creator)
