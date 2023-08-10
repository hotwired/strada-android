package dev.hotwire.strada

object Strada {
    val config: StradaConfig = StradaConfig()

    fun userAgentSubstring(componentFactories: List<BridgeComponentFactory<*,*>>): String {
        val components = componentFactories.joinToString(" ") { it.name }
        return "bridge-components: [$components]"
    }
}
