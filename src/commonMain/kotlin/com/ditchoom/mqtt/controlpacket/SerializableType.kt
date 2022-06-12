@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_OVERRIDE")

package com.ditchoom.mqtt.controlpacket

import com.ditchoom.buffer.ReadBuffer
import com.ditchoom.buffer.WriteBuffer
import com.ditchoom.mqtt.controlpacket.ControlPacket.Companion.readMqttUtf8StringNotValidatedSized
import com.ditchoom.mqtt.controlpacket.ControlPacket.Companion.readVariableByteInteger
import com.ditchoom.mqtt.controlpacket.ControlPacket.Companion.writeMqttUtf8String
import com.ditchoom.mqtt.controlpacket.ControlPacket.Companion.writeVariableByteInteger
import kotlin.reflect.KClass

interface MqttSerializationStrategy<T> {
    fun serialize(obj: T, writeBuffer: WriteBuffer)
}

interface MqttDeserializationStrategy<T> {
    fun deserialize(readBuffer: ReadBuffer): T? = null
}


interface MqttSerializable<T : Any> : MqttSerializationStrategy<T>, MqttDeserializationStrategy<T>

object ByteArraySerializer : MqttSerializable<ByteArray> {
    override fun serialize(obj: ByteArray, writeBuffer: WriteBuffer) {
        writeBuffer.writeVariableByteInteger(obj.size)
        writeBuffer.write(obj)
    }

    override fun deserialize(readBuffer: ReadBuffer) = readBuffer.readByteArray(readBuffer.readVariableByteInteger())
}

object StringSerializer : MqttSerializable<String> {
    override fun serialize(obj: String, writeBuffer: WriteBuffer) {
        writeBuffer.writeMqttUtf8String(obj)
    }

    override fun deserialize(readBuffer: ReadBuffer) = readBuffer.readMqttUtf8StringNotValidatedSized().toString()
}

val serializers = mutableMapOf<KClass<*>, MqttSerializable<*>>(
    Pair(ByteArray::class, ByteArraySerializer),
    Pair(String::class, StringSerializer)
)

inline fun <reified T : Any> findSerializer(): MqttSerializable<T>? {
    @Suppress("UNCHECKED_CAST")
    return serializers[T::class] as? MqttSerializable<T>?
}

fun <T : Any> findSerializer(kClass: KClass<T>): MqttSerializable<T>? {
    @Suppress("UNCHECKED_CAST")
    return serializers[kClass] as? MqttSerializable<T>?
}

inline fun <reified T : Any> installSerializer(serializable: MqttSerializable<T>) {
    serializers[T::class] = serializable
}
