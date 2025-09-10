package com.example.mocklocation.core.helper

import java.io.FilterInputStream
import java.io.InputStream

class CountingInputStream(
    input: InputStream,
    private val onBytesRead: (Long) -> Unit
) : FilterInputStream(input) {
    private var totalBytesRead = 0L
    override fun read(): Int {
        val byte = super.read()
        if (byte != -1) {
            totalBytesRead++
            onBytesRead(totalBytesRead)
        }
        return byte
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val count = super.read(b, off, len)
        if (count != -1) {
            totalBytesRead += count
            onBytesRead(totalBytesRead)
        }
        return count
    }
}