@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

interface ISubscribeAcknowledgement : ControlPacket {
    val packetIdentifier: Int
}