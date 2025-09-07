package com.example.mocklocation.data.parser

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

class ByteReader(private val buffer: ByteBuffer) {
    companion object {
        val EUC_KR: Charset = Charset.forName("euc-kr")
    }

    init { buffer.order(ByteOrder.LITTLE_ENDIAN) }

    fun position(): Int = buffer.position()
    fun position(newPosition: Int): Buffer = buffer.position(newPosition)
    fun readByte(): Byte = buffer.get()
    fun readShort(): Short = buffer.short
    fun readUnsignedShort(): Int = buffer.char.code
    fun readInt(): Int = buffer.int

    fun readString(length: Int, charset: Charset = EUC_KR, trimNull: Boolean = true): String {
        val bytes = ByteArray(length)
        buffer.get(bytes)
        val result = String(bytes, charset)
        return if (trimNull) result.replace("\u0000", "").trim() else result
    }
}