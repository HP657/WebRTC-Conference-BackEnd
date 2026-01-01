package com.hp657.webrtcconference

import com.hp657.webrtcconference.port.`in`.SignalingUseCase
import com.hp657.webrtcconference.port.out.UserSessionRepository
import com.hp657.webrtcconference.service.SignalingService
import com.hp657.webrtcconference.websocket.WebSocketMessageSender
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun signalingUseCase(
        userSessionRepository: UserSessionRepository,
        webSocketMessageSender: WebSocketMessageSender
    ): SignalingUseCase {
        return SignalingService(userSessionRepository, webSocketMessageSender)
    }
}