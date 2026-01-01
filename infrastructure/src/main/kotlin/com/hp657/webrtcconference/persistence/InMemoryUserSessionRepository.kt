package com.hp657.webrtcconference.persistence

import com.hp657.webrtcconference.model.UserSession
import com.hp657.webrtcconference.port.out.UserSessionRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryUserSessionRepository : UserSessionRepository {

    private val sessions = ConcurrentHashMap<String, UserSession>()
    private val webSocketSessionIdMap = ConcurrentHashMap<String, String>()
    private val userIdMap = ConcurrentHashMap<String, String>()

    override fun save(userSession: UserSession) {
        sessions[userSession.sessionId] = userSession
        userSession.webSocketSessionId?.let { webSocketSessionIdMap[it] = userSession.sessionId }
        userIdMap[userSession.userId] = userSession.sessionId
    }

    override fun findByUserId(userId: String): UserSession? {
        return userIdMap[userId]?.let { sessions[it] }
    }

    override fun findByWebSocketSessionId(webSocketSessionId: String): UserSession? {
        return webSocketSessionIdMap[webSocketSessionId]?.let { sessions[it] }
    }

    override fun deleteByWebSocketSessionId(webSocketSessionId: String) {
        webSocketSessionIdMap.remove(webSocketSessionId)?.let { sessionId ->
            sessions.remove(sessionId)?.let { userSession ->
                userIdMap.remove(userSession.userId)
            }
        }
    }

    override fun findAll(): List<UserSession> {
        return sessions.values.toList()
    }
}