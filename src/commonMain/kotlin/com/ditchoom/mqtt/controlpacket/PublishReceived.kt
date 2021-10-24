@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

interface IPublishReceived : ControlPacket {
    val packetIdentifier: Int
    fun expectedResponse(): IPublishRelease
}