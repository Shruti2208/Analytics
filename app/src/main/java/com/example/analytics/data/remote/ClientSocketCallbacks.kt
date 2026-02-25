package com.example.analytics.data.remote

import com.example.analytics.data.model.IncomingRequestModel
import com.neovisionaries.ws.client.WebSocketException

interface ClientSocketCallbacks {
    fun handleRequest(incomingRequest: IncomingRequestModel)
    fun serverResponse(response: String)
    fun webSocketOnError(incomingRequest: IncomingRequestModel, error: WebSocketException)
    fun handleSocketCommsError(incomingRequest: IncomingRequestModel, error: String)
    fun handleAuthenticationError(incomingRequest: IncomingRequestModel, error: String)
}
