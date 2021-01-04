# Quick Start Guide

## Load the Bridge

The primary object is `Bridge`. You create a `Bridge` using the `WebView` instance in your app:

```kotlin
val bridge = Bridge(webView)
```

Load the `Bridge` into the current `WebView` page when the `WebViewClient`'s `onPageFinished()` is called:

```kotlin
webView.webViewClient = object : WebViewClient() {
  override fun onPageFinished(view: WebView?, url: String?) {
    super.onPageFinished(view, url)
    bridge.load()
  }
}
```

This injects the bundled strada.js file and setups everything needed for communicating with the web bridge.

### Receiving
To receive messages from the web bridge, you want to set a `delegate` on the bridge.

```kotlin
bridge.delegate = this

// BridgeDelegate
override fun bridgeDidInitialize() {
  // Configure your supported components
}

override fun bridgeDidReceiveMessage(message: Message) {
  // Inspect message and perform related actions
}
```

### Sending
You send over the bridge by creating a `Message` and calling `send`:

```kotlin
bridge.send(message)
```

This can be a new message, or a message previously received from the web bridge to "reply" to a message.
