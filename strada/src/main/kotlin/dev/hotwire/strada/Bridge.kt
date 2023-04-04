package dev.hotwire.strada

import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.serialization.json.JsonElement

// These need to match whatever is set in strada.js
private const val bridgeGlobal = "window.nativeBridge"
private const val bridgeJavascriptInterface = "Strada"

@Suppress("unused")
class Bridge(val webView: WebView) {
    internal var repository = Repository()
    private var componentsAreRegistered: Boolean = false

    var delegate: BridgeDelegate? = null

    init {
        // The JavascriptInterface must be added before the page is loaded
        webView.addJavascriptInterface(this, bridgeJavascriptInterface)
    }

    fun register(component: String) {
        val javascript = generateJavaScript("register", component.toJsonElement())
        evaluate(javascript)
    }

    fun register(components: List<String>) {
        val javascript = generateJavaScript("register", components.toJsonElement())
        evaluate(javascript)
    }

    fun unregister(component: String) {
        val javascript = generateJavaScript("unregister", component.toJsonElement())
        evaluate(javascript)
    }

    fun send(message: Message) {
        logMessage("bridgeWillSendMessage", message)
        val internalMessage = InternalMessage.fromMessage(message)
        val javascript = generateJavaScript("send", internalMessage.toJson().toJsonElement())
        evaluate(javascript)
    }

    fun load() {
        evaluate(userScript())
    }

    fun reset() {
        componentsAreRegistered = false
    }

    fun isReady(): Boolean {
        return componentsAreRegistered
    }

    @JavascriptInterface
    fun bridgeDidInitialize() {
        logEvent("bridgeDidInitialize")
        runOnUiThread {
            delegate?.bridgeDidInitialize()
        }
    }

    @JavascriptInterface
    fun bridgeDidUpdateSupportedComponents() {
        logEvent("bridgeDidUpdateSupportedComponents")
        componentsAreRegistered = true
    }

    @JavascriptInterface
    fun bridgeDidReceiveMessage(message: String?) {
        runOnUiThread {
            InternalMessage.fromJson(message)?.let {
                delegate?.bridgeDidReceiveMessage(it.toMessage())
            }
        }
    }

    // Internal

    internal fun userScript(): String {
        return repository.getUserScript(webView.context)
    }

    internal fun evaluate(javascript: String) {
        logEvent("evaluatingJavascript", javascript)
        webView.evaluateJavascript(javascript) {}
    }

    internal fun generateJavaScript(bridgeFunction: String, vararg arguments: JsonElement): String {
        val functionName = sanitizeFunctionName(bridgeFunction)
        val encodedArguments = encode(arguments.toList())
        return "$bridgeGlobal.$functionName($encodedArguments)"
    }

    internal fun encode(arguments: List<JsonElement>): String {
        return arguments.joinToString(",") { it.toJson() }
    }

    internal fun sanitizeFunctionName(name: String): String {
        return name.removeSuffix("()")
    }
}
