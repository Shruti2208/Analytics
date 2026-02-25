package com.example.analytics.data.remote

import com.example.analytics.data.model.IncomingRequestModel

// TODO: Replace with the actual WebSocket implementation from your services package
class WebSocketClient(private val callbacks: ClientSocketCallbacks) {

    fun connectToServer(incomingRequest: IncomingRequestModel) {
        // TODO: implement — open WebSocket connection, then call callbacks.handleRequest(incomingRequest)
        callbacks.handleRequest(incomingRequest)
    }

    fun sendToserver(message: String) {
        // TODO: implement — send message over the open WebSocket
    }

    fun closeWebsocket() {
        // TODO: implement — close the WebSocket connection
    }
}
