package com.ditchoom.mqtt.controlpacket

interface IUnsubscribeRequest : ControlPacket {
    val packetIdentifier: Int
    val topics: Set<CharSequence>
}
