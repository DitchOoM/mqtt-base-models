@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

interface IPublishRelease : ControlPacket {
    val packetIdentifier: Int
    fun expectedResponse(): IPublishComplete
}