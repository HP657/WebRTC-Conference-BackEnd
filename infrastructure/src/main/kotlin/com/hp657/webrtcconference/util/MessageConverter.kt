package com.hp657.webrtcconference.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.hp657.webrtcconference.model.SignalMessage
import org.springframework.stereotype.Component

@Component
class MessageConverter {
    val objectMapper = ObjectMapper()
        .registerModule(
            KotlinModule.Builder()
                .build()
        )
    fun toJson(message: SignalMessage): String {
        return objectMapper.writeValueAsString(message)
    }

    fun fromJson(json: String): SignalMessage {
        return objectMapper.readValue(json)
    }
}