package com.example.mocklocation.data.parser

import com.example.mocklocation.data.dto.InfoIndex
import com.example.mocklocation.data.dto.MapInfo
import com.example.mocklocation.data.dto.RouteSearchInfo
import com.example.mocklocation.data.dto.Tvas
import com.example.mocklocation.data.dto.TvasHeader
import com.example.mocklocation.data.dto.TvasVersion
import com.example.mocklocation.data.dto.detailInfo
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.parser.GpsDataParser
import timber.log.Timber
import java.nio.ByteBuffer
import javax.inject.Inject
import android.util.Base64
import com.example.mocklocation.data.dto.TileData
import com.example.mocklocation.data.dto.TileHeader
import com.example.mocklocation.data.dto.TileList
import com.example.mocklocation.data.dto.VertexData
import com.example.mocklocation.data.dto.VertexHeader
import com.example.mocklocation.data.dto.VertexList
import com.example.mocklocation.data.dto.toGpsPoint

enum class SectionId(val id: String) {
    TILE("TI"),
    VERTEX("VX")
}

/**
 * TVAS(ByteArray) → GpsPoint 리스트 파싱기
 * 내부적으로 TvasToWgs84Converter를 사용하여 좌표 변환 수행
 */
class TvasKotlinParser @Inject constructor(
    private val tileParser: TileSectionParser,
    private val vertexParser: VertexSectionParser,
    private val converter: TvasToWgs84Converter
) : GpsDataParser<String> {
    override fun parse(input: String): List<GpsPoint> {
        val tvasBytes = Base64.decode(input, Base64.DEFAULT)
        val tvas = parseTvas(tvasBytes, tileParser, vertexParser) ?: return emptyList()
        val wgsPoints = converter.convert(tvas)
        Timber.d(tvas.detailInfo())

        return wgsPoints.map { it.toGpsPoint() }
    }
}

private fun parseTvas(
    bytes: ByteArray,
    tileParser: TileSectionParser,
    vertexParser: VertexSectionParser
): Tvas? {
    if (bytes.isEmpty()) return null
    val reader = ByteReader(ByteBuffer.wrap(bytes))

    try {
        val header = HeaderParser.parse(reader)
        val headerEndPosition = reader.position()

        val tileInfoIndex = header.infoIndexes.find { it.id == SectionId.TILE.id }
        val vertexInfoIndex = header.infoIndexes.find { it.id == SectionId.VERTEX.id }

        val tileInfo = tileInfoIndex?.let {
            reader.position(headerEndPosition + it.offset)
            tileParser.parse(reader)
        }
        val vertexInfo = vertexInfoIndex?.let {
            reader.position(headerEndPosition + it.offset)
            vertexParser.parse(reader)
        }

        Timber.d("헤더 파싱 성공. 헤더 종료 위치: $headerEndPosition")
        Timber.d("타일 섹션 파싱 결과: ${if (tileInfo == null) "실패" else "성공"}")
        Timber.d("보간점 섹션 파싱 결과: ${if (vertexInfo == null) "실패" else "성공"}")

        return Tvas(
            header = header,
            tileInfo = tileInfo,
            vertexInfo = vertexInfo
        )
    } catch (e: Exception) {
        Timber.d("TvasParser 파싱 중 예외 발생: ${e.message}", e)
        return null
    }
}

object HeaderParser {
    fun parse(reader: ByteReader): TvasHeader {
        val totalSize = reader.readInt()
        val version = TvasVersion(
            tvasFormatVersion = reader.readByte(),
            reserved = reader.readByte(),
            language = reader.readByte(),
            charset = reader.readByte(),
            mapVersion = reader.readString(8, Charsets.US_ASCII, false)
        )

        val routeSearchInfo = RouteSearchInfo(
            routePlanOptionCode = reader.readByte(),
            fareWeightOptionCode = reader.readByte(),
            departDayType = reader.readByte(),
            nextDayType = reader.readByte(),
            totalDistance = reader.readInt(),
            totalTime = reader.readInt(),
            taxiFare = reader.readInt(),
            controlRouteFlag = reader.readByte(),
            routeType = reader.readByte(),
            alternativeBranchPoint = reader.readShort(),
            destinationLocationInfo = reader.readByte(),
            routeChangeFlag = reader.readByte(),
            evRangeReachable = reader.readByte(),
            reserved = reader.readByte()
        )

        val mapInfo = MapInfo(
            coordinateSystem = reader.readByte(),
            tiles = reader.readByte(),
            reserved3 = reader.readShort(),
            departY = reader.readInt(),
            departX = reader.readInt(),
            destX = reader.readInt(),
            destY = reader.readInt(),
            departName = reader.readString(100),
            destName = reader.readString(100),
            totalInfoIndexCount = reader.readShort(),
            reserved4 = reader.readShort()
        )

        val infoIndexes = List(mapInfo.totalInfoIndexCount.toInt()) {
            InfoIndex(
                id = reader.readString(4, Charsets.US_ASCII),
                offset = reader.readInt(),
                size = reader.readInt()
            )
        }
        return TvasHeader(totalSize, version, routeSearchInfo, mapInfo, infoIndexes)
    }
}

object TileSectionParser {
    val id = SectionId.TILE

    fun parse(reader: ByteReader): TileList {
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

object VertexSectionParser {
    val id = SectionId.VERTEX

    fun parse(reader: ByteReader): VertexList {
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