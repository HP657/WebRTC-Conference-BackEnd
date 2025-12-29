package com.hp657.webrtcconference.service

import com.hp657.webrtcconference.model.SignalMessage
import com.hp657.webrtcconference.model.UserSession
import com.hp657.webrtcconference.port.`in`.SignalingUseCase
import com.hp657.webrtcconference.port.out.SignalingMessageSender
import com.hp657.webrtcconference.port.out.UserSessionRepository
import org.springframework.stereotype.Service

@Service
class SignalingService(
    private val userSessionRepository: UserSessionRepository,
    private val signalingMessageSender: SignalingMessageSender
) : SignalingUseCase {

    override fun handleSignal(senderSessionId: String, message: SignalMessage) {
        val sender = userSessionRepository.findByWebSocketSessionId(senderSessionId)
            ?: throw IllegalArgumentException("Sender session not found: $senderSessionId")

        when (message) {
            is SignalMessage.Offer -> {
                val targetUserSession = userSessionRepository.findByUserId(message.targetUserId)
                if (targetUserSession != null && targetUserSession.webSocketSessionId != null) {
                    signalingMessageSender.sendSignal(targetUserSession.webSocketSessionId!!, message)
                } else {
                    // Handle error: target user not found or not connected
                    println("Target user ${message.targetUserId} not found or not connected.")
                }
            }
            is SignalMessage.Answer -> {
                val targetUserSession = userSessionRepository.findByUserId(message.targetUserId)
                if (targetUserSession != null && targetUserSession.webSocketSessionId != null) {
                    signalingMessageSender.sendSignal(targetUserSession.webSocketSessionId!!, message)
                } else {
                    println("Target user ${message.targetUserId} not found or not connected.")
                }
            }
            is SignalMessage.IceCandidate -> {
                val targetUserSession = userSessionRepository.findByUserId(message.targetUserId)
                if (targetUserSession != null && targetUserSession.webSocketSessionId != null) {
                    signalingMessageSender.sendSignal(targetUserSession.webSocketSessionId!!, message)
                } else {
                    println("Target user ${message.targetUserId} not found or not connected.")
                }
            }
            is SignalMessage.Join -> {
                // Notify other participants about the new user
                userSessionRepository.findAll()
                    .filter { it.webSocketSessionId != null && it.webSocketSessionId != senderSessionId }
                    .forEach {
                        signalingMessageSender.sendSignal(it.webSocketSessionId!!, message)
                    }
            }
            is SignalMessage.Leave -> {
                // Notify other participants about the user leaving
                userSessionRepository.findAll()
                    .filter { it.webSocketSessionId != null && it.webSocketSessionId != senderSessionId }
                    .forEach {
                        signalingMessageSender.sendSignal(it.webSocketSessionId!!, message)
                    }
            }
        }
    }

    override fun registerUser(userId: String, webSocketSessionId: String): String {
        var userSession = userSessionRepository.findByUserId(userId)
        if (userSession == null) {
            userSession = UserSession(userId = userId, webSocketSessionId = webSocketSessionId)
        } else {
            userSession.webSocketSessionId = webSocketSessionId
        }
        userSessionRepository.save(userSession)
        return userSession.sessionId
    }

    override fun unregisterUser(webSocketSessionId: String) {
        userSessionRepository.deleteByWebSocketSessionId(webSocketSessionId)
    }
}
