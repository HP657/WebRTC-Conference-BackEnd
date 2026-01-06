package com.hp657.webrtcconference.config

import com.hp657.webrtcconference.usecase.SignalingUseCase
import com.hp657.webrtcconference.port.UserSessionRepository
import com.hp657.webrtcconference.usecase.SignalingUseCaseImpl
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
        return SignalingUseCaseImpl(userSessionRepository, webSocketMessageSender)
    }
}