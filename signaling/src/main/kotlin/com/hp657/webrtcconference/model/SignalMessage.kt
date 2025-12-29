package com.hp657.webrtcconference.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = SignalMessage.Offer::class, name = "offer"),
    JsonSubTypes.Type(value = SignalMessage.Answer::class, name = "answer"),
    JsonSubTypes.Type(value = SignalMessage.IceCandidate::class, name = "iceCandidate"),
    JsonSubTypes.Type(value = SignalMessage.Join::class, name = "join"),
    JsonSubTypes.Type(value = SignalMessage.Leave::class, name = "leave")
)
sealed class SignalMessage {

    data class Offer(
        val sdp: String,
        val targetUserId: String
    ) : SignalMessage()

    data class Answer(
        val sdp: String,
        val targetUserId: String
    ) : SignalMessage()

    data class IceCandidate(
        val sdpMid: String?,
        val sdpMLineIndex: Int?,
        val candidate: String,
        val targetUserId: String
    ) : SignalMessage()

    data class Join(
        val userId: String
    ) : SignalMessage()

    data class Leave(
        val userId: String
    ) : SignalMessage()
}
