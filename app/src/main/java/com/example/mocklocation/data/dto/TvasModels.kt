package com.example.mocklocation.data.dto

data class Tvas(
    val header: TvasHeader,
    val tileInfo: TileList?,
    val vertexInfo: VertexList?
)

data class TvasHeader(
    val totalSize: Int,
    val tvasVersion: TvasVersion,
    val routeSearchInfo: RouteSearchInfo,
    val mapInfo: MapInfo,
    val infoIndexes: List<InfoIndex>
)

data class TvasVersion(
    val tvasFormatVersion: Byte,
    val reserved: Byte,
    val language: Byte,
    val charset: Byte,
    val mapVersion: String
)

data class RouteSearchInfo(
    val routePlanOptionCode: Byte,
    val fareWeightOptionCode: Byte,
    val departDayType: Byte,
    val nextDayType: Byte,
    val totalDistance: Int,
    val totalTime: Int,
    val taxiFare: Int,
    val controlRouteFlag: Byte,
    val routeType: Byte, /** 1: 추천경로, 2: 대안경로, 3: 테마로드 */
    val alternativeBranchPoint: Short,
    val destinationLocationInfo: Byte,
    val routeChangeFlag: Byte,
    val evRangeReachable: Byte,
    val reserved: Byte
)

data class MapInfo(
    val coordinateSystem: Byte,
    val tiles: Byte,
    val reserved3: Short,
    val departX: Int,
    val departY: Int,
    val destX: Int,
    val destY: Int,
    val departName: String,
    val destName: String,
    val totalInfoIndexCount: Short,
    val reserved4: Short
)

data class InfoIndex(
    val id: String,
    val offset: Int,
    val size: Int
)

data class TileList(
    val header: TileHeader,
    val tiles: List<TileData>
)

data class TileHeader(
    val totalCount: Short,
    val infoIndexType: Byte,
    val reserved: Byte,
    val infoId: String,
)

data class TileData(
    val tileCode: Int,
    val lastVertexIndex: Int,
    val reserved: Short
)

data class VertexList(
    val header: VertexHeader,
    val vertices: List<VertexData>
)

data class VertexHeader(
    val totalCount: Short,
    val infoIndexType: Byte,
    val reserved: Byte,
    val infoId: String,
)

data class VertexData(
    val x: Int,
    val y: Int,
    val distanceBetweenVertex: Short,
    val timeBetweenVertex: Short
)

fun Tvas.detailInfo(): String {
    fun formatDist(m: Int): String =
        if (m >= 1000) String.format("%.2f km", m / 1000.0) else "${m} m"

    fun formatTime(sec: Int): String {
        if (sec < 0) return "${sec}s"
        val m = sec / 60
        val s = sec % 60
        return if (m > 0) "${m}분 ${s}초" else "${s}초"
    }

    val totalVertices = vertexInfo?.vertices?.size ?: 0
    val totalTiles = tileInfo?.tiles?.size ?: 0
    val log = StringBuilder()

    log.append("-------------------- 경로 기본 정보 ---------------------\n")
    log.append(" 포맷 버전: ${header.tvasVersion.tvasFormatVersion}\n")
    log.append(" 언어 코드: ${header.tvasVersion.language} (0=한국어)\n")
    log.append(" 문자 인코딩: ${header.tvasVersion.charset} (0=EUC-KR, 1=UTF-8)\n")
    log.append(" 지도 버전: ${header.tvasVersion.mapVersion}\n")
    log.append(" 총 거리: ${formatDist(header.routeSearchInfo.totalDistance)}\n")
    log.append(" 총 시간: ${formatTime(header.routeSearchInfo.totalTime)}\n")
    log.append(" 예상 택시 요금: ${header.routeSearchInfo.taxiFare} 원\n")
    log.append(" 출발지: ${header.mapInfo.departName} (좌표: ${header.mapInfo.departX}, ${header.mapInfo.departY})\n")
    log.append(" 도착지: ${header.mapInfo.destName} (좌표: ${header.mapInfo.destX}, ${header.mapInfo.destY})\n")

    log.append("----------------- 데이터 인덱스 목록 (${header.infoIndexes.size}개) -----------------\n")
    header.infoIndexes.take(2).forEachIndexed { i, idx ->
        log.append(" ${i + 1}. ID: '${idx.id}' | 오프셋: ${idx.offset} | 크기: ${idx.size}바이트\n")
    }

    log.append("------------------ 타일 정보 (TI) - 총 ${totalTiles}개 -------------------\n")
    if (tileInfo?.tiles.isNullOrEmpty()) {
        log.append("  타일 데이터 없음\n")
    } else {
        tileInfo?.tiles?.forEachIndexed { i, t ->
            log.append(" ${i + 1}. 타일코드: ${t.tileCode} | 마지막 보간점 인덱스: ${t.lastVertexIndex}\n")
        }
    }

    log.append("--------------- 보간점 정보 (VX) - 총 ${totalVertices}개 ----------------\n")
    if (vertexInfo?.vertices.isNullOrEmpty()) {
        log.append("  보간점 없음\n")
    } else {
        vertexInfo?.vertices?.forEachIndexed { i, v ->
            log.append(" #${i + 1} 좌표: (${v.x}, ${v.y}) | 거리: ${formatDist(v.distanceBetweenVertex.toInt())} | 시간: ${formatTime(v.timeBetweenVertex.toInt())}\n")
        }
    }
    log.append("---------------------------------------------------\n")
    return log.toString()
}