package com.hp657.webrtcconference.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hp657.webrtcconference.model.SignalMessage
import com.hp657.webrtcconference.port.SignalingMessageSender
import com.hp657.webrtcconference.port.WebSocketMessageSenderPort
import com.hp657.webrtcconference.util.SignalMessageConverter
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class WebSocketMessageSender(
    private val messageConverter: SignalMessageConverter
) : SignalingMessageSender, WebSocketMessageSenderPort {

    private val sessions = ConcurrentHashMap<String, WebSocketSession>()
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun addSession(session: WebSocketSession) {
        sessions[session.id] = session
        println("➕ Session added: ${session.id}, total: ${sessions.size}")
    }

    fun removeSession(sessionId: String) {
        sessions.remove(sessionId)
        println("➖ Session removed: $sessionId, total: ${sessions.size}")
    }

    override fun sendSignal(webSocketSessionId: String, message: SignalMessage) {
        val session = sessions[webSocketSessionId]
        if (session != null && session.isOpen) {
            try {
                synchronized(session) {
                    val jsonMessage = messageConverter.toJson(message)
                    session.sendMessage(TextMessage(jsonMessage))
                    println("📤 Signal sent to $webSocketSessionId")
                }
            } catch (e: Exception) {
                println("❌ Failed to send signal: ${e.message}")
            }
        } else {
            println("⚠️ WebSocket session $webSocketSessionId not found or not open")
        }
    }

    override fun sendCustomMessage(webSocketSessionId: String, data: Map<String, Any?>) {
        val session = sessions[webSocketSessionId]
        if (session != null && session.isOpen) {
            try {
                synchronized(session) {
                    val jsonMessage = objectMapper.writeValueAsString(data)
                    session.sendMessage(TextMessage(jsonMessage))
                    println("📤 Custom message sent to $webSocketSessionId: ${data["type"]}")
                }
            } catch (e: Exception) {
                println("❌ Failed to send custom message: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("⚠️ WebSocket session $webSocketSessionId not found or not open")
        }
    }

    override fun broadcastCustomMessage(data: Map<String, Any?>) {
        val jsonMessage = objectMapper.writeValueAsString(data)
        var successCount = 0
        var failCount = 0

        sessions.values.forEach { session ->
            if (session.isOpen) {
                try {
                    synchronized(session) {
                        session.sendMessage(TextMessage(jsonMessage))
                        successCount++
                    }
                } catch (e: Exception) {
                    println("❌ Failed to broadcast to ${session.id}: ${e.message}")
                    failCount++
                }
            }
        }
        println("📢 Broadcast ${data["type"]}: $successCount success, $failCount failed")
    }
}