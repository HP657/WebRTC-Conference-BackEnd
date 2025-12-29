package com.hp657.webrtcconference.websocket

import com.hp657.webrtcconference.model.SignalMessage
import com.hp657.webrtcconference.port.out.SignalingMessageSender
import com.hp657.webrtcconference.util.MessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketMessageSender(
    private val messageConverter: MessageConverter
) : SignalingMessageSender {

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    fun addSession(session: WebSocketSession) {
        sessions[session.id] = session
    }

    fun removeSession(sessionId: String) {
        sessions.remove(sessionId)
    }

    override fun sendSignal(webSocketSessionId: String, message: SignalMessage) {
        val session = sessions[webSocketSessionId]
        if (session != null && session.isOpen) {
            val jsonMessage = messageConverter.toJson(message)
            session.sendMessage(TextMessage(jsonMessage))
        } else {
            println("WebSocket session $webSocketSessionId not found or not open. Cannot send message: $message")
        }
    }
}