package com.example.mocklocation.data.parser

import com.example.mocklocation.data.dto.TileData
import com.example.mocklocation.data.dto.TileHeader
import com.example.mocklocation.data.dto.TileList
import com.example.mocklocation.data.dto.VertexData
import com.example.mocklocation.data.dto.VertexHeader
import com.example.mocklocation.data.dto.VertexList

object TileSectionParser : SectionParser<TileList> {
    override val id = SectionId.TILE

    override fun parse(reader: ByteReader): TileList {
        val totalCount = reader.readShort()
        val infoIndexType = reader.readByte()
        val reserved = reader.readByte()
        val infoId = reader.readString(4, Charsets.US_ASCII, true)

        val tiles = List(totalCount.toInt()) {
            val tileCode = reader.readInt()
            val lastVertexIdx = reader.readUnsignedShort()
            val reservedShort = reader.readShort()
            TileData(tileCode, lastVertexIdx, reservedShort)
        }

        return TileList(
            TileHeader(totalCount, infoIndexType, reserved, infoId),
            tiles
        )
    }
}

object VertexSectionParser : SectionParser<VertexList> {
    override val id = SectionId.VERTEX

    override fun parse(reader: ByteReader): VertexList {
        val totalCount = reader.readShort()
        val infoIndexType = reader.readByte()
        val reserved = reader.readByte()
        val infoId = reader.readString(4, Charsets.US_ASCII, true)

        val vertices = List(totalCount.toInt()) {
            val x = reader.readUnsignedShort()
            val y = reader.readUnsignedShort()
            val distance = reader.readShort()
            val time = reader.readShort()
            VertexData(x, y, distance, time)
        }

        return VertexList(
            VertexHeader(totalCount, infoIndexType, reserved, infoId),
            vertices
        )
    }
}