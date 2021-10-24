@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

import com.ditchoom.buffer.PlatformBuffer
import com.ditchoom.buffer.ReadBuffer
import com.ditchoom.mqtt.controlpacket.ControlPacket.Companion.readVariableByteInteger

interface ControlPacketFactory {
    fun from(buffer: ReadBuffer): ControlPacket {
        val byte1 = buffer.readUnsignedByte()
        val remainingLength = buffer.readVariableByteInteger()
        return from(buffer, byte1, remainingLength)
    }

    fun from(buffer: ReadBuffer, byte1: UByte, remainingLength: UInt): ControlPacket

    fun pingRequest(): IPingRequest
    fun pingResponse(): IPingResponse

    fun publish(
        dup: Boolean = false,
        qos: QualityOfService = QualityOfService.EXACTLY_ONCE,
        packetIdentifier: Int? = null,
        retain: Boolean = false,
        topicName: CharSequence,
        payload: PlatformBuffer? = null,
        // MQTT 5 Properties
        payloadFormatIndicator: Boolean = false,
        messageExpiryInterval: Long? = null,
        topicAlias: Int? = null,
        responseTopic: CharSequence? = null,
        correlationData: PlatformBuffer? = null,
        userProperty: List<Pair<CharSequence, CharSequence>> = emptyList(),
        subscriptionIdentifier: Set<Long> = emptySet(),
        contentType: CharSequence? = null
    ): IPublishMessage

}