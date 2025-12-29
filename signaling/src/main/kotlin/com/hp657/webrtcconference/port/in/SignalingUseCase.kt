package com.hp657.webrtcconference.port.`in`

import com.hp657.webrtcconference.model.SignalMessage

interface SignalingUseCase {
    fun handleSignal(senderSessionId: String, message: SignalMessage)
    fun registerUser(userId: String, webSocketSessionId: String): String
    fun unregisterUser(webSocketSessionId: String)
}
