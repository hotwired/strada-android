package com.basecamp.strata

import android.content.Context

internal class Repository {
    fun getUserScript(context: Context): String {
        return context.assets.open("js/strata.js").use {
            String(it.readBytes())
        }
    }
}
