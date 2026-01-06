package com.hp657.webrtcconference.usecase

import com.hp657.webrtcconference.model.SignalMessage
import com.hp657.webrtcconference.model.UserSession
import com.hp657.webrtcconference.usecase.SignalingUseCase
import com.hp657.webrtcconference.port.UserSessionRepository
import com.hp657.webrtcconference.port.WebSocketMessageSenderPort

class SignalingUseCaseImpl(
    private val userSessionRepository: UserSessionRepository,
    private val webSocketMessageSenderPort: WebSocketMessageSenderPort
) : SignalingUseCase {

    override fun registerUser(userId: String, webSocketSessionId: String): String {
        println("👤 Registering user: $userId with session: $webSocketSessionId")
        val userSession = UserSession(
            userId = userId,
            webSocketSessionId = webSocketSessionId
        )
        userSessionRepository.save(userSession)

        broadcastUserList()

        return userSession.sessionId
    }

    override fun unregisterUser(webSocketSessionId: String) {
        println("👋 Unregistering user with session: $webSocketSessionId")
        userSessionRepository.deleteByWebSocketSessionId(webSocketSessionId)

        broadcastUserList()
    }

    override fun handleSignal(senderSessionId: String, message: SignalMessage) {
        val senderSession = userSessionRepository.findByWebSocketSessionId(senderSessionId)

        if (senderSession == null) {
            println("❌ Sender session not found: $senderSessionId")
            return
        }

        println("📨 Handling signal: ${message::class.simpleName} from ${senderSession.userId}")

        try {
            when (message) {
                is SignalMessage.Offer -> {
                    println("📞 Processing Offer: ${senderSession.userId} -> ${message.targetUserId}")
                    val targetSession = userSessionRepository.findByUserId(message.targetUserId)
                    val targetWebSocketId = targetSession?.webSocketSessionId
                    if (targetWebSocketId != null) {
                        webSocketMessageSenderPort.sendCustomMessage(targetWebSocketId, mapOf(
                            "type" to "offer",
                            "sdp" to message.sdp,
                            "callerUserId" to senderSession.userId
                        ))
                        println("✅ Offer sent successfully")
                    } else {
                        println("❌ Target user not found: ${message.targetUserId}")
                    }
                }
                is SignalMessage.Answer -> {
                    println("📞 Processing Answer: ${senderSession.userId} -> ${message.targetUserId}")
                    val targetSession = userSessionRepository.findByUserId(message.targetUserId)
                    val targetWebSocketId = targetSession?.webSocketSessionId
                    if (targetWebSocketId != null) {
                        webSocketMessageSenderPort.sendCustomMessage(targetWebSocketId, mapOf(
                            "type" to "answer",
                            "sdp" to message.sdp
                        ))
                        println("✅ Answer sent successfully")
                    } else {
                        println("❌ Target user not found: ${message.targetUserId}")
                    }
                }
                is SignalMessage.IceCandidate -> {
                    // Trickle ICE 비활성화 - ICE candidates는 무시
                    println("🧊 ICE candidate received but ignored (using complete SDP)")
                }
                is SignalMessage.Call -> {
                    println("📞 Call: ${message.callerUserId} -> ${message.calleeUserId}")
                    val targetSession = userSessionRepository.findByUserId(message.calleeUserId)
                    val targetWebSocketId = targetSession?.webSocketSessionId
                    if (targetWebSocketId != null) {
                        webSocketMessageSenderPort.sendCustomMessage(targetWebSocketId, mapOf(
                            "type" to "call",
                            "callerUserId" to message.callerUserId,
                            "calleeUserId" to message.calleeUserId
                        ))
                        println("✅ Call signal sent")
                    }
                }
                is SignalMessage.AcceptCall -> {
                    println("✅ Accept Call: ${message.calleeUserId} -> ${message.callerUserId}")
                    val targetSession = userSessionRepository.findByUserId(message.callerUserId)
                    val targetWebSocketId = targetSession?.webSocketSessionId
                    if (targetWebSocketId != null) {
                        webSocketMessageSenderPort.sendCustomMessage(targetWebSocketId, mapOf(
                            "type" to "acceptCall",
                            "callerUserId" to message.callerUserId,
                            "calleeUserId" to message.calleeUserId
                        ))
                        println("✅ Accept call signal sent")
                    }
                }
                is SignalMessage.CancelCall -> {
                    println("❌ Cancel Call")
                    val targetSession = userSessionRepository.findByUserId(message.calleeUserId)
                    val targetWebSocketId = targetSession?.webSocketSessionId
                    if (targetWebSocketId != null) {
                        webSocketMessageSenderPort.sendCustomMessage(targetWebSocketId, mapOf(
                            "type" to "cancelCall",
                            "callerUserId" to message.callerUserId,
                            "calleeUserId" to message.calleeUserId
                        ))
                    }
                }
                is SignalMessage.HangUp -> {
                    println("📴 Hang Up from ${message.userId}")
                    userSessionRepository.findAll().forEach { session ->
                        val webSocketId = session.webSocketSessionId
                        if (webSocketId != null && webSocketId != senderSessionId) {
                            webSocketMessageSenderPort.sendCustomMessage(webSocketId, mapOf(
                                "type" to "hangUp",
                                "userId" to message.userId
                            ))
                        }
                    }
                }
                else -> {
                    println("❓ Unknown message type: $message")
                }
            }
        } catch (e: Exception) {
            println("❌ Error processing signal: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun broadcastUserList() {
        try {
            val allUsers = userSessionRepository.findAll().map { it.userId }
            println("📋 Broadcasting user list: $allUsers")

            webSocketMessageSenderPort.broadcastCustomMessage(mapOf(
                "type" to "userList",
                "users" to allUsers
            ))
        } catch (e: Exception) {
            println("❌ Error broadcasting user list: ${e.message}")
            e.printStackTrace()
        }
    }
}