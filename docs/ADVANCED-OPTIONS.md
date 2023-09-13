# Advanced Options

## Enable Debug Logging
During development, you may want to see what `strada-android` is doing behind the scenes. To enable debug logging, call `Strada.config.debugLoggingEnabled = true`. Debug logging should always be disabled in your production app. For example:

```kotlin
if (BuildConfig.DEBUG) {
    Strada.config.debugLoggingEnabled = true
}
```
