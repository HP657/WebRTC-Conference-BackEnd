package com.hp657.webrtcconference.model

import java.util.UUID

data class UserSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val userId: String,
    var webSocketSessionId: String? = null
)
