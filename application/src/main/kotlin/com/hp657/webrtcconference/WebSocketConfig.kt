package com.hp657.webrtcconference

import com.hp657.webrtcconference.presentation.SignalingHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean
import org.springframework.context.annotation.Bean

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val signalingHandler: SignalingHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(signalingHandler, "/signal")
            .setAllowedOrigins("*")
    }

    @Bean
    fun createWebSocketContainer(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(64 * 1024) // 64KB
        container.setMaxBinaryMessageBufferSize(64 * 1024)
        container.setMaxSessionIdleTimeout(10 * 60 * 1000L) // 10분
        container.setAsyncSendTimeout(5000L) // 5초
        return container
    }
}