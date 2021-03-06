package com.ditchoom.mqtt.topic

import com.ditchoom.mqtt.controlpacket.IPublishMessage
import com.ditchoom.mqtt.controlpacket.MqttSerializable
import com.ditchoom.mqtt.controlpacket.findSerializer
import kotlin.reflect.KClass

data class CallbackTypeReference<T : Any>(
    val callback: SubscriptionCallback<T>,
    val klass: KClass<T>,
    val serializer: MqttSerializable<T>? = findSerializer(klass)
) {
    fun handleCallback(incomingPublish: IPublishMessage) {
//        val payload = null //incomingPublish.payloadPacket()
        val topic = incomingPublish.topic
        val qos = incomingPublish.qualityOfService
//        if (payload == null) {
        callback.onMessageReceived(Name(topic), qos, null)
//            return
//        }
//        if (serializer == null) {
//            println("Failed to find serializer for payload $callback")
//            callback.onMessageReceived(Name(topic), qos, null)
//            return
//        }
//        val message = serializer.deserialize(payload)
//        callback.onMessageReceived(Name(topic), qos, message)
    }
}
