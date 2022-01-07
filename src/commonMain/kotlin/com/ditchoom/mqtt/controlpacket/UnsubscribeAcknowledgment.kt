package com.ditchoom.mqtt.controlpacket

interface IUnsubscribeAcknowledgment : ControlPacket {
    val packetIdentifier: Int
}