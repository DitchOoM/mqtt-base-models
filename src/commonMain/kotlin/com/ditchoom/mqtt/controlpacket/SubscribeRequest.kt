@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

import com.ditchoom.mqtt.topic.Filter

interface ISubscribeRequest : ControlPacket {
    val packetIdentifier: Int
    fun expectedResponse(): ISubscribeAcknowledgement
    fun getTopics(): List<Filter>

    companion object {
        const val controlPacketValue: Byte = 8
    }
}
