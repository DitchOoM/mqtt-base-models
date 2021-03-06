@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package com.ditchoom.mqtt.topic

import com.ditchoom.mqtt.controlpacket.MqttUtf8String
import kotlin.jvm.JvmInline

/**
 * The topic level separator is used to introduce structure into the Topic Name. If present, it divides the Topic Name
 * into multiple “topic levels”.
 *
 * A subscription’s Topic Filter can contain special wildcard characters, which allow a Client to subscribe to multiple
 * topics at once.
 *
 * The wildcard characters can be used in Topic Filters, but MUST NOT be used within a Topic Name
 */
@JvmInline
value class Name(val topic: CharSequence) {

    fun validate(asServer: Boolean = false): Node? {
        return try {
            val rootNode = Node.from(MqttUtf8String(topic))
            if (rootNode.isWildcard) {
                return null
            }
            if (!asServer && rootNode.level.value.startsWith('$')) {
                return null
            }
            rootNode
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Validate and convert the string topic into a TopicLevelNode structure
     */
    fun validateTopic(asServer: Boolean = false): TopicLevelNode? {
        return try {
            val rootNode = TopicLevelNode.from(MqttUtf8String(topic))
            if (rootNode.hasWildcardInTopic()) {
                return null
            }
            if (!asServer && rootNode.value.value.startsWith('$')) {
                return null
            }
            rootNode
        } catch (e: Exception) {
            null
        }
    }

    fun toFilter() = Filter(topic)
}
