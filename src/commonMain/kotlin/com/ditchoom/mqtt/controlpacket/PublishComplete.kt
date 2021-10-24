@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

interface IPublishComplete : ControlPacket {
    val packetIdentifier: Int
}