package com.hp657.webrtcconference.presentation

import com.hp657.webrtcconference.websocket.WebSocketMessageSender
import com.hp657.webrtcconference.util.MessageConverter
import com.hp657.webrtcconference.port.`in`.SignalingUseCase
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Component
class SignalingHandler(
    private val signalingUseCase: SignalingUseCase,
    private val messageConverter: MessageConverter,
    private val webSocketMessageSender: WebSocketMessageSender
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        try {
            val query = session.uri?.query
            val userId = query
                ?.split("&")
                ?.map { it.split("=") }
                ?.firstOrNull { it[0] == "userId" }
                ?.getOrNull(1)
                ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                ?: session.id

            webSocketMessageSender.addSession(session)
            println("✅ WebSocket connection established: ${session.id}, userId: $userId")
            signalingUseCase.registerUser(userId, session.id)
        } catch (e: Exception) {
            println("❌ Error in afterConnectionEstablished: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            println("📥 Received message from ${session.id}: ${message.payload.take(100)}...")
            val signalMessage = messageConverter.fromJson(message.payload)
            signalingUseCase.handleSignal(session.id, signalMessage)
        } catch (e: Exception) {
            println("❌ Error handling message from ${session.id}: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        try {
            println("🔌 WebSocket connection closed: ${session.id}, code: ${status.code}, reason: ${status.reason}")
            webSocketMessageSender.removeSession(session.id)
            signalingUseCase.unregisterUser(session.id)
        } catch (e: Exception) {
            println("❌ Error in afterConnectionClosed: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        println("⚠️ WebSocket transport error for session ${session.id}: ${exception.message}")
        exception.printStackTrace()
    }

    override fun supportsPartialMessages(): Boolean {
        return false
    }
}