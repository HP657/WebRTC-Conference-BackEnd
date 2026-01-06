package com.hp657.webrtcconference.port

import com.hp657.webrtcconference.model.UserSession

interface UserSessionRepository {
    fun save(userSession: UserSession)
    fun findByUserId(userId: String): UserSession?
    fun findByWebSocketSessionId(webSocketSessionId: String): UserSession?
    fun deleteByWebSocketSessionId(webSocketSessionId: String)
    fun findAll(): List<UserSession>
}
