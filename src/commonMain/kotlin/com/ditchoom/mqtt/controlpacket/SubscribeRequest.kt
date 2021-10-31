@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

interface ISubscribeRequest : ControlPacket {
    val packetIdentifier: Int
    fun expectedResponse(): ISubscribeAcknowledgement
    val subscriptions: Set<ISubscription>

    companion object {
        const val controlPacketValue: Byte = 8
    }
}
