package com.hp657.webrtcconference.usecase

import com.hp657.webrtcconference.model.SignalMessage

interface SignalingUseCase {
    fun handleSignal(senderSessionId: String, message: SignalMessage)
    fun registerUser(userId: String, webSocketSessionId: String): String
    fun unregisterUser(webSocketSessionId: String)
}
