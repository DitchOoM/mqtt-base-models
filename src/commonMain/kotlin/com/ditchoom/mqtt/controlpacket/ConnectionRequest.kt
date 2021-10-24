package com.ditchoom.mqtt.controlpacket

interface IConnectionRequest : ControlPacket {
    val keepAliveTimeoutSeconds: UShort
    val clientIdentifier: CharSequence
    val cleanStart: Boolean
    val username: CharSequence?
    val protocolName: CharSequence
    val protocolVersion: Int
    fun copy(): IConnectionRequest
}