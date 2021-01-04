package com.basecamp.strata

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal fun Any.toJson(): String {
    return gson.toJson(this)
}

internal fun <T> String.toObject(typeToken: TypeToken<T>): T {
    return gson.fromJson(this, typeToken.type)
}

private val gson: Gson = Gson()
