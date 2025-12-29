package com.hp657.webrtcconference

import com.hp657.webrtcconference.port.`in`.SignalingUseCase
import com.hp657.webrtcconference.port.out.SignalingMessageSender
import com.hp657.webrtcconference.port.out.UserSessionRepository
import com.hp657.webrtcconference.service.SignalingService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun signalingUseCase(
        userSessionRepository: UserSessionRepository,
        signalingMessageSender: SignalingMessageSender
    ): SignalingUseCase {
        return SignalingService(userSessionRepository, signalingMessageSender)
    }
}
