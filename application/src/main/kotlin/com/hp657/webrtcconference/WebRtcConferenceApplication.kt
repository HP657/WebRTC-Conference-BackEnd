package com.hp657.webrtcconference

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebRtcConferenceApplication

fun main(args: Array<String>) {
    runApplication<WebRtcConferenceApplication>(*args)
}