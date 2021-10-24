@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

interface IPublishAcknowledgment : ControlPacket {
    val packetIdentifier: Int
}