@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package com.ditchoom.mqtt.topic

import com.ditchoom.buffer.Parcelable
import com.ditchoom.mqtt.Parcelize
import com.ditchoom.mqtt.controlpacket.MqttUtf8String

@Parcelize
data class Filter(val topicFilter: CharSequence) : Parcelable {
    fun validate(asServer: Boolean = false): Node? {
        return try {
            val rootNode = Node.from(MqttUtf8String(topicFilter))
            if (!asServer && rootNode.level.value.startsWith('$')) {
                return null
            }
            rootNode
        } catch (e: Exception) {
            null
        }
    }

}
