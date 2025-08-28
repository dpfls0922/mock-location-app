package com.example.mocklocation.data.parser

interface SectionParser<T : Any> {
    val id: SectionId
    fun parse(reader: ByteReader): T
}

enum class SectionId(val id: String) {
    TILE("TI"),
    VERTEX("VX")
}