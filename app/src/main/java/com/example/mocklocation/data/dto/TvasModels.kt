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

    return buildString {
        appendLine("📍 경로 기본 정보")
        appendLine("- 포맷 버전: ${header.tvasVersion.tvasFormatVersion}")
        appendLine("- 언어 코드: ${header.tvasVersion.language} (0=한국어)")
        appendLine("- 문자 인코딩 코드: ${header.tvasVersion.charset} (0=EUC-KR, 1=UTF-8)")
        appendLine("- 지도 버전: ${header.tvasVersion.mapVersion}")
        appendLine("- 총 거리: ${formatDist(header.routeSearchInfo.totalDistance)}")
        appendLine("- 총 시간: ${formatTime(header.routeSearchInfo.totalTime)}")
        appendLine("- 예상 택시 요금: ${header.routeSearchInfo.taxiFare} 원")
        appendLine("- 출발지: ${header.mapInfo.departName} (${header.mapInfo.departX})")
        appendLine("- 도착지: ${header.mapInfo.destName} (${header.mapInfo.destX})")

        appendLine()
        appendLine("📂 데이터 인덱스 목록 (${header.infoIndexes.size}개)")
        header.infoIndexes.take(2).forEachIndexed { i, idx ->
            appendLine("${i + 1}. ID='${idx.id}' | 오프셋=${idx.offset} | 크기=${idx.size}바이트")
        }

        appendLine()
        appendLine("🗺 타일 정보 (TI)")
        if (tileInfo?.tiles.isNullOrEmpty()) {
            appendLine("- 타일 데이터 없음")
        } else {
            tileInfo?.tiles?.take(5)?.forEachIndexed { i, t ->
                appendLine("${i + 1}. 타일코드=${t.tileCode} | 마지막 보간점 인덱스=${t.lastVertexIndex}")
            }
            if ((tileInfo?.tiles?.size ?: 0) > 5) appendLine("...")
        }

        appendLine()
        appendLine("📌 보간점 정보 (VX) - 총 ${vertexInfo?.vertices?.size ?: 0}개")
        if (vertexInfo?.vertices.isNullOrEmpty()) {
            appendLine("- 보간점 없음")
        } else {
            vertexInfo?.vertices?.take(5)?.forEachIndexed { i, v ->
                appendLine("#${i + 1} 좌표=(${v.x}, ${v.y}) | 거리=${formatDist(v.distanceBetweenVertex.toInt())} | 시간=${formatTime(v.timeBetweenVertex.toInt())}")
            }
            if ((vertexInfo?.vertices?.size ?: 0) > 5) appendLine("...")
        }
    }
}