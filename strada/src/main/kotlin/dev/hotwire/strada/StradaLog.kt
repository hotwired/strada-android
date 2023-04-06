package dev.hotwire.strada

import android.util.Log

@Suppress("unused")
object StradaLog {
    private const val DEFAULT_TAG = "StradaLog"

    /**
     * Enable debug logging to see message communication from/to the WebView.
     */
    var debugLoggingEnabled = false

    internal fun d(msg: String) = log(Log.DEBUG, DEFAULT_TAG, msg)

    internal fun e(msg: String) = log(Log.ERROR, DEFAULT_TAG, msg)

    private fun log(logLevel: Int, tag: String, msg: String) {
        when (logLevel) {
            Log.DEBUG -> if (debugLoggingEnabled) Log.d(tag, msg)
            Log.ERROR -> Log.e(tag, msg)
        }
    }
}

internal fun logMessage(event: String, message: Message) {
    logEvent(event, message.toString())
}

internal fun logEvent(event: String, details: String = "") {
    StradaLog.d("$event ".padEnd(35, '.') + " [$details]")
}
