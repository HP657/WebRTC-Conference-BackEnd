package com.hp657.webrtcconference.presentation

import com.hp657.webrtcconference.websocket.WebSocketMessageSender
import com.hp657.webrtcconference.util.MessageConverter
import com.hp657.webrtcconference.port.`in`.SignalingUseCase
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class SignalingHandler(
    private val signalingUseCase: SignalingUseCase,
    private val messageConverter: MessageConverter,
    private val webSocketMessageSender: WebSocketMessageSender
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        webSocketMessageSender.addSession(session)
        println("WebSocket connection established: ${session.id}")
        // For simplicity, let's assume userId is passed as a query parameter or similar
        // For now, we'll use session.id as a placeholder for userId
        val userId = session.attributes["userId"] as? String ?: session.id
        signalingUseCase.registerUser(userId, session.id)
        println("User $userId registered with session ${session.id}")
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val signalMessage = messageConverter.fromJson(message.payload)
            signalingUseCase.handleSignal(session.id, signalMessage)
        } catch (e: Exception) {
            println("Error handling message from ${session.id}: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        webSocketMessageSender.removeSession(session.id)
        signalingUseCase.unregisterUser(session.id)
        println("WebSocket connection closed: ${session.id} with status ${status.code}")
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        println("WebSocket transport error for session ${session.id}: ${exception.message}")
        exception.printStackTrace()
    }
}