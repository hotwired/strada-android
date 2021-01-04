package com.basecamp.strata

interface BridgeDelegate {
    fun bridgeDidInitialize()
    fun bridgeDidReceiveMessage(message: Message)
}
