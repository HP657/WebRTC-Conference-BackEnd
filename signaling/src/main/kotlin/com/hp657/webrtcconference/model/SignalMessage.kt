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
    JsonSubTypes.Type(value = SignalMessage.Leave::class, name = "leave"),
    JsonSubTypes.Type(value = SignalMessage.Call::class, name = "call"),
    JsonSubTypes.Type(value = SignalMessage.CancelCall::class, name = "cancelCall"),
    JsonSubTypes.Type(value = SignalMessage.AcceptCall::class, name = "acceptCall"),
    JsonSubTypes.Type(value = SignalMessage.HangUp::class, name = "hangUp")
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

    data class Call(
        val callerUserId: String,
        val calleeUserId: String
    ) : SignalMessage()

    data class CancelCall(
        val callerUserId: String,
        val calleeUserId: String
    ) : SignalMessage()

    data class AcceptCall(
        val callerUserId: String,
        val calleeUserId: String
    ) : SignalMessage()

    data class HangUp(
        val userId: String
    ) : SignalMessage()
}