# Advanced Options

## Enable Debug Logging
During development, you may want to see what `strada-android` is doing behind the scenes. To enable debug logging, call `Strada.config.debugLoggingEnabled = true`. Debug logging should always be disabled in your production app. For example:

```kotlin
if (BuildConfig.DEBUG) {
    Strada.config.debugLoggingEnabled = true
}
```

## Using a custom de/serialization library
If you'd prefer not to use `strada-android`'s built-in `KotlinXJsonConverter` using [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html#example-json-serialization) to automatically de/serialize `Message` `data`, you can implement your own type converter class. Here's an example implementing your custom `StradaJsonTypeConverter` using [Moshi](https://github.com/square/moshi). 

**`BridgeJsonConverter.kt`**
```kotlin
class BridgeJsonConverter(val moshi: Moshi) : StradaJsonTypeConverter() {
    override fun <T> toObject(jsonData: String, type: Class<T>): T? {
        return try {
            moshi.adapter(type).fromJson(jsonData)
        } catch (e: Exception) {
            // Log exception
            null
        }
    }

    override fun <T> toJson(data: T, type: Class<T>): String {
        return moshi.adapter(type).toJson(data)
    }
}
```

Then, configure your custom converter class:

```kotlin
Strada.config.jsonConverter = BridgeJsonConverter(moshi)
```
