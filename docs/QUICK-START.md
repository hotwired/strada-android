# Quick Start Guide

_You can find the code in this guide fully implemented in the `turbo-android` [demo app](https://github.com/hotwired/turbo-android/tree/main/demo)._

## Configuration

There are a few things you need to initially configure in your [`turbo-android`](https://github.com/hotwired/turbo-android) app to integrate `strada-android`. 

### Create a list of registered bridge components

For now, create an empty (global) list of registered component factories, so we have a reference. You'll need to populate this list with each bridge component that your app supports.

**`BridgeComponentFactories.kt`:**
```kotlin
val bridgeComponentFactories = listOf(
    // Add registered components here later
)
```

### Initialize the WebView instance

For Strada to work properly across your web and native app, you'll need to make sure each `TurboSession` `WebView` instance is initialized with the following:
- An updated user agent string that includes the supported bridge components. Strada provides a utility function that builds the substring for you.
- Initialize the `WebView` with the `Bridge` class, so Strada can internally manage the `WebView` through the app's lifecycle.

The place to do this is in each `TurboSessionNavHostFragment` in your app:

**`MainSessionNavHostFragment.kt`:**
```kotlin
class MainSessionNavHostFragment : TurboSessionNavHostFragment() {
    
    // ...

    override fun onSessionCreated() {
        super.onSessionCreated()

        // Initialize the user agent
        session.webView.settings.userAgentString = session.webView.customUserAgent

        // Initialize Strada bridge with new WebView instance
        Bridge.initialize(session.webView)
    }

    // Build a custom user agent string. Be careful to continue to include 
    // the "Turbo Native" substring as part of the user agent.
    private val WebView.customUserAgent: String
        get() {
            val turboSubstring = Turbo.userAgentSubstring()
            val stradaSubstring = Strada.userAgentSubstring(bridgeComponentFactories)
            return "$turboSubstring; $stradaSubstring; ${settings.userAgentString}"
        }
}
```

### Set a JSON converter
Strada passes messages with json serialized `data` between the `WebView` and native code. If you'd like to take advantage of automatic de/serialization between your own `data` models and `strada-android`, you must set a converter class that implements `StradaJsonConverter`. We suggest using the [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html#example-json-serialization) library. If you decide to use `kotlinx.serialization`, `strada-android` provides an automatic `KotlinXJsonConverter()` class that you can use with no extra work:

**`MainActivity.kt`:**
```kotlin
class MainActivity : AppCompatActivity(), TurboActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configStrada()
        // ...
    }

    private fun configStrada() {
        Strada.config.jsonConverter = KotlinXJsonConverter()
    }
}
```

Note that you'll need to still need to include `kotlinx.serialization` as a dependency in your app to annotate your models with its `@Serializable` and `@SerialName` annotations.

If you'd rather use another de/serialization library like [Moshi](https://github.com/square/moshi), see the [advanced options](ADVANCED-OPTIONS.md) page.


## Implement the `BridgeDestination` interface
You'll need to implement the `BridgeDestination` interface where your `TurboNavDestination` is present:

**`NavDestination.kt`:**
```kotlin
interface NavDestination : TurboNavDestination, BridgeDestination {
    
    // ...

    override fun bridgeWebViewIsReady(): Boolean {
        return session.isReady
    }
}
```

## Delegate to the `BridgeDelegate` class
You'll need to delegate the `TurboWebFragment` lifecycle callbacks to the `BridgeDelegate` class:

**`WebFragment.kt`:**
```kotlin
class WebFragment : TurboWebFragment(), NavDestination {
    private val bridgeDelegate by lazy {
        BridgeDelegate(
            location = location,
            destination = this,
            componentFactories = bridgeComponentFactories
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(bridgeDelegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(bridgeDelegate)
    }

    override fun onColdBootPageStarted(location: String) {
        bridgeDelegate.onColdBootPageStarted()
    }

    override fun onColdBootPageCompleted(location: String) {
        bridgeDelegate.onColdBootPageCompleted()
    }

    override fun onWebViewAttached(webView: TurboWebView) {
        bridgeDelegate.onWebViewAttached(webView)
    }

    override fun onWebViewDetached(webView: TurboWebView) {
        bridgeDelegate.onWebViewDetached()
    }
    
    // ...
}
```


## Build your first `BridgeComponent` 

`// TODO`



