package com.hp657.webrtcconference.port

interface WebSocketMessageSenderPort {
    fun sendCustomMessage(webSocketSessionId: String, data: Map<String, Any?>)
    fun broadcastCustomMessage(data: Map<String, Any?>)
}