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
    fun packetSize() = 2 + remainingLength()
    fun remainingLength() = 0

    fun validateOrGetWarning(): MqttWarning? = null

    fun serialize(writeBuffer: WriteBuffer) {
        fixedHeader(writeBuffer)
        variableHeader(writeBuffer)
        payload(writeBuffer)
    }

    companion object {

        private val VARIABLE_BYTE_INT_MAX = 268435455

        fun isValidFirstByte(uByte: UByte): Boolean {
            val byte1AsUInt = uByte.toUInt()
            return byte1AsUInt.shr(4).toInt() in 1..15
        }


        fun WriteBuffer.writeVariableByteInteger(int: Int): WriteBuffer {
            if (int !in 0..VARIABLE_BYTE_INT_MAX) {
                throw MalformedInvalidVariableByteInteger(int)
            }
            var numBytes = 0
            var no = int.toLong()
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

        fun ReadBuffer.readMqttUtf8StringNotValidatedSized(): Pair<Int, CharSequence> {
            val length = readUnsignedShort().toInt()
            val decoded = readUtf8(length)
            return Pair(length, decoded)
        }

        fun ReadBuffer.readVariableByteInteger(): Int {
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
                throw MalformedInvalidVariableByteInteger(value.toInt())
            }
            if (value < 0 || value > VARIABLE_BYTE_INT_MAX.toLong()) {
                throw MalformedInvalidVariableByteInteger(value.toInt())
            }
            return value.toInt()
        }


        fun variableByteSize(int: Int): Byte {
            if (int !in 0..VARIABLE_BYTE_INT_MAX) {
                throw MalformedInvalidVariableByteInteger(int)
            }
            var numBytes = 0.toByte()
            var no = int
            do {
                no /= 128
                numBytes++
            } while (no > 0 && numBytes < 4)
            return numBytes
        }
    }
}
