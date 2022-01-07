package com.ditchoom.mqtt.controlpacket

interface IPublishMessage : ControlPacket {
    val qualityOfService: QualityOfService
    val topic: CharSequence
    val packetIdentifier: Int?
    fun expectedResponse(): ControlPacket?

    companion object {
        const val controlPacketValue: Byte = 3
    }
}
