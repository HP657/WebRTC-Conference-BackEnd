package com.hp657.webrtcconference.port.out

interface WebSocketMessageSenderPort {
    fun sendCustomMessage(webSocketSessionId: String, data: Map<String, Any?>)
    fun broadcastCustomMessage(data: Map<String, Any?>)
}