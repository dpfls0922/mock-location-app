package com.example.mocklocation.data.parser

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class ByteReader(private val buffer: ByteBuffer) {
    init { buffer.order(ByteOrder.LITTLE_ENDIAN) }

    fun position(): Int = buffer.position()
    fun position(newPosition: Int): Buffer = buffer.position(newPosition)
    fun readByte(): Byte = buffer.get()
    fun readShort(): Short = buffer.short
    fun readUnsignedShort(): Int = buffer.short.toInt() and 0xFFFF
    fun readInt(): Int = buffer.int

    fun readString(length: Int, charset: Charset = Charset.forName("euc-kr"), trimNull: Boolean = true): String {
        val bytes = ByteArray(length)
        buffer.get(bytes)
        val result = String(bytes, charset)
        return if (trimNull) result.replace("\u0000", "").trim() else result
    }
}