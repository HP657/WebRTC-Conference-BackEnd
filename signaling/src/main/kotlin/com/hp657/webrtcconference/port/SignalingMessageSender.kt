package com.hp657.webrtcconference.port

import com.hp657.webrtcconference.model.SignalMessage


interface SignalingMessageSender {
    fun sendSignal(webSocketSessionId: String, message: SignalMessage)
}
