package dev.hotwire.strada

abstract class StradaJsonEncoder {
    abstract fun <T> toObject(jsonData: String, type: Class<T>): T?
    abstract fun <T> toJson(data: T, type: Class<T>): String
}
