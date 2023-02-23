package dev.hotwire.strada

interface BridgeComponentFactory<in R : BridgeDelegate, out T : BridgeComponent> {
    val name: String
    fun create(delegate: R): T
}
