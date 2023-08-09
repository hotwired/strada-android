package dev.hotwire.strada

abstract class StradaJsonConverter {
    abstract fun <T> toObject(jsonData: String, type: Class<T>): T?
    abstract fun <T> toJson(data: T, type: Class<T>): String
}
