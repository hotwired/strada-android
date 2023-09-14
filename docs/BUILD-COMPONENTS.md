# Build Bridge Components

## Your first component

After you set up your app in the [Quick Start](QUICK-START.md) guide, it's time to build your first native bridge component. Native components receive messages from corresponding web components of the same `name`. So, be sure to understand how [web components](https://strada.hotwired.dev/handbook/web) work in your web app and start there.

Once a component receives a message, it uses that message's `event` and `data` to perform custom native functionality. If the user performs a native action, the native component can reply back to the corresponding web component using the originally received `message` and (optionally) new `data`.  

You create your first native component by subclassing the `BridgeComponent` class. The example below is from the `FormComponent` in the `turbo-android` [demo app](https://github.com/hotwired/turbo-android/tree/main/demo).  It'll look like this:

**`FormComponent.kt`**
```kotlin
class FormComponent(
    name: String,
    private val delegate: BridgeDelegate<NavDestination>
) : BridgeComponent<NavDestination>(name, delegate) {
    // ...
}
```

## Handle received messages

Every component must implement the `onReceive(message: Message)` function. Each `message` has an `event` associated with it, so you should first look at the `event` to determine how to handle the incoming `message`. Here's how the `FormComponent` handles receiving messages:

**`FormComponent.kt`**
```kotlin
class FormComponent(
    name: String,
    private val delegate: BridgeDelegate<NavDestination>
) : BridgeComponent<NavDestination>(name, delegate) {
    
    override fun onReceive(message: Message) {
        // Handle incoming messages based on the message `event`.
        when (message.event) {
            "connect" -> handleConnectEvent(message)
            "submitEnabled" -> handleSubmitEnabled()
            "submitDisabled" -> handleSubmitDisabled()
            else -> Log.w("TurboDemo", "Unknown event for message: $message")
        }
    }

    private fun handleConnectEvent(message: Message) {
        val data = message.data<MessageData>() ?: return
        
        // Write native code to display a native submit button in the 
        // toolbar displayed in the delegate.destination. Use the 
        // incoming data.title to set the button title. The 
        // implementation depends on how your app is structured.
    }
    
    private fun handleSubmitEnabled() {
        // Write code to enable the submit button.
    }
    
    private fun handleSubmitDisabled() {
        // Write code to disable the submit button.
    }

    // Use kotlinx.serialization annotations to define a serializable
    // data class that represents the incoming message.data json.
    @Serializable
    data class MessageData(
        @SerialName("submitTitle") val title: String
    )
}
```

## Reply to received messages

If you'd like to inform the corresponding web component that an action has occurred, such as the user clicking on a submit button, you can reply to the originally received message. For the `FormComponent` it looks like this:

**`FormComponent.kt`**
```kotlin
class FormComponent(
    name: String,
    private val delegate: BridgeDelegate<NavDestination>
) : BridgeComponent<NavDestination>(name, delegate) {
    
    // ...

    private fun showToolbarButton(data: MessageData) {
        // ...

        binding.formSubmit.apply {
            text = data.title
            setOnClickListener {
                performSubmit()
            }
        }
    }

    // Reply to the originally received "connect" event message (without any new data).
    private fun performSubmit(): Boolean {
        return replyTo("connect")
    }
}
```

When a web component receives a reply from a sent messages, it can run a callback to perform the appropriate action in the web app. In this example, tapping on the native submit button and sending back a reply results in the web `"form"` component clicking the hidden web submit button in its form.

For convenience, there are multiple ways to reply to received messages:

```kotlin
replyTo("eventName")
replyTo("eventName", newData)
replyWith(originalMessage)
replyWith(originalMessage.replacing(data = newData))
replyWith(originalMessage.replacing(event = "newEventName"))
```

## Register your component

For every component that you want to use in your app, you must register it in the list you created in the [Quick Start](QUICK-START.md) guide. This allows the web app and backend (through the `WebView` user-agent) know what components are natively registered for the current version of the app. To register the new `FormComponent`, it looks like this:

**`BridgeComponentFactories.kt`**
```kotlin
val bridgeComponentFactories = listOf(
    BridgeComponentFactory("form", ::FormComponent),
    // ...
)
```

The `name` (`"form"` in this instance) that you give to each component must be unique and match the name of the web component that it corresponds to.
