@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

import com.ditchoom.buffer.Parcelable
import com.ditchoom.buffer.PlatformBuffer
import com.ditchoom.buffer.ReadBuffer
import com.ditchoom.mqtt.controlpacket.ControlPacket.Companion.readVariableByteInteger
import com.ditchoom.mqtt.controlpacket.ISubscription.RetainHandling
import com.ditchoom.mqtt.controlpacket.format.ReasonCode

interface ControlPacketFactory : Parcelable {
    fun from(buffer: ReadBuffer): ControlPacket {
        val byte1 = buffer.readUnsignedByte()
        val remainingLength = buffer.readVariableByteInteger()
        return from(buffer, byte1, remainingLength)
    }

    fun from(buffer: ReadBuffer, byte1: UByte, remainingLength: Int): ControlPacket

    fun pingRequest(): IPingRequest
    fun pingResponse(): IPingResponse

    fun subscribe(
        packetIdentifier: Int,
        topicFilter: CharSequence,
        maximumQos: QualityOfService = QualityOfService.AT_LEAST_ONCE,
        noLocal: Boolean = false,
        retainAsPublished: Boolean = false,
        retainHandling: RetainHandling = RetainHandling.SEND_RETAINED_MESSAGES_AT_TIME_OF_SUBSCRIBE,
        serverReference: CharSequence? = null,
        userProperty: List<Pair<CharSequence, CharSequence>> = emptyList(),
    ): ISubscribeRequest

    fun subscribe(
        packetIdentifier: Int,
        subscriptions: Set<ISubscription>,
        serverReference: CharSequence? = null,
        userProperty: List<Pair<CharSequence, CharSequence>> = emptyList(),
    ): ISubscribeRequest

    fun publish(
        dup: Boolean = false,
        qos: QualityOfService = QualityOfService.AT_MOST_ONCE,
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

    fun unsubscribe(
        packetIdentifier: Int,
        topic: CharSequence,
        userProperty: List<Pair<CharSequence, CharSequence>> = emptyList()
    ) = unsubscribe(packetIdentifier, setOf(topic), userProperty)

    fun unsubscribe(
        packetIdentifier: Int,
        topics: Set<CharSequence>,
        userProperty: List<Pair<CharSequence, CharSequence>> = emptyList()
    ): IUnsubscribeRequest

    fun disconnect(
        reasonCode: ReasonCode = ReasonCode.NORMAL_DISCONNECTION,
        sessionExpiryIntervalSeconds: Long? = null,
        reasonString: CharSequence? = null,
        userProperty: List<Pair<CharSequence, CharSequence>> = emptyList(),
    ): IDisconnectNotification


}