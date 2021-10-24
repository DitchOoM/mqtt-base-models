package com.ditchoom.mqtt.controlpacket

interface IConnectionAcknowledgment : ControlPacket {
    val isSuccessful: Boolean
    val connectionReason: String
    val sessionPresent: Boolean
}
