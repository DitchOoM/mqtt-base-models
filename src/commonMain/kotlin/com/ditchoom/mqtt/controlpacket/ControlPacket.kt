@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ditchoom.mqtt.controlpacket

import com.ditchoom.buffer.Parcelable
import com.ditchoom.buffer.ReadBuffer
import com.ditchoom.buffer.WriteBuffer
import com.ditchoom.mqtt.MalformedInvalidVariableByteInteger
import com.ditchoom.mqtt.MqttWarning
import com.ditchoom.mqtt.controlpacket.format.fixed.DirectionOfFlow
import kotlin.experimental.and
import kotlin.experimental.or

interface ControlPacket : Parcelable {
    val controlPacketValue: Byte
    val direction: DirectionOfFlow
    val flags: Byte get() = 0b0
    val mqttVersion: Byte

    val controlPacketFactory: ControlPacketFactory

    private fun fixedHeader(writeBuffer: WriteBuffer) {
        val packetValueUInt = controlPacketValue.toUInt()
        val packetValueShifted = packetValueUInt.shl(4)
        val localFlagsByte = flags.toUByte().toInt()
        val byte1 = (packetValueShifted.toByte() + localFlagsByte).toUByte()
        writeBuffer.write(byte1)
        val remaining = remainingLength()
        writeBuffer.writeVariableByteInteger(remaining)
    }

    fun variableHeader(writeBuffer: WriteBuffer) {}
    fun payload(writeBuffer: WriteBuffer) {}
    fun packetSize() = 2u + remainingLength()
    fun remainingLength() = 0u

    fun validateOrGetWarning(): MqttWarning? = null

    fun serialize(writeBuffer: WriteBuffer) {
        fixedHeader(writeBuffer)
        variableHeader(writeBuffer)
        payload(writeBuffer)
    }

    companion object {

        private val VARIABLE_BYTE_INT_MAX = 268435455.toUInt()

        fun isValidFirstByte(uByte: UByte): Boolean {
            val byte1AsUInt = uByte.toUInt()
            return byte1AsUInt.shr(4).toInt() in 1..15
        }


        fun WriteBuffer.writeVariableByteInteger(uInt: UInt): WriteBuffer {
            if (uInt !in 0.toUInt()..VARIABLE_BYTE_INT_MAX) {
                throw MalformedInvalidVariableByteInteger(uInt)
            }
            var numBytes = 0
            var no = uInt.toLong()
            do {
                var digit = (no % 128).toByte()
                no /= 128
                if (no > 0) {
                    digit = digit or 0x80.toByte()
                }
                write(digit)
                numBytes++
            } while (no > 0 && numBytes < 4)
            return this
        }

        fun WriteBuffer.writeMqttUtf8String(charSequence: CharSequence): WriteBuffer {
            val string = charSequence.toString()
            val size = string.utf8Length().toUShort()
            write(size)
            writeUtf8(string)
            return this
        }

        fun ReadBuffer.readMqttUtf8StringNotValidatedSized(): Pair<UInt, CharSequence> {
            val length = readUnsignedShort().toUInt()
            val decoded = readUtf8(length)
            return Pair(length, decoded)
        }

        fun ReadBuffer.readVariableByteInteger(): UInt {
            var digit: Byte
            var value = 0L
            var multiplier = 1L
            var count = 0L
            try {
                do {
                    digit = readByte()
                    count++
                    value += (digit and 0x7F).toLong() * multiplier
                    multiplier *= 128
                } while ((digit and 0x80.toByte()).toInt() != 0)
            } catch (e: Exception) {
                throw MalformedInvalidVariableByteInteger(value.toUInt())
            }
            if (value < 0 || value > VARIABLE_BYTE_INT_MAX.toLong()) {
                throw MalformedInvalidVariableByteInteger(value.toUInt())
            }
            return value.toUInt()
        }


        fun variableByteSize(uInt: UInt): UByte {
            if (uInt !in 0.toUInt()..VARIABLE_BYTE_INT_MAX) {
                throw MalformedInvalidVariableByteInteger(uInt)
            }
            var numBytes = 0
            var no = uInt.toLong()
            do {
                no /= 128
                numBytes++
            } while (no > 0 && numBytes < 4)
            return numBytes.toUByte()
        }
    }
}
