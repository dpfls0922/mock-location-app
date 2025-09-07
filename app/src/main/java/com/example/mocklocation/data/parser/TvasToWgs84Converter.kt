package com.example.mocklocation.data.parser

import com.example.mocklocation.data.dto.Tvas
import com.example.mocklocation.di.DefaultDispatcher
import com.example.mocklocation.domain.entity.SKCoord
import com.example.mocklocation.domain.entity.TileOrigin
import com.skt.tmap.vsm.data.VSMMapPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.*

class TvasToWgs84Converter @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend fun convert(tvas: Tvas): List<VSMMapPoint> = withContext(defaultDispatcher) {
        val tiles = tvas.tileInfo?.tiles.orEmpty()
        val vertices = tvas.vertexInfo?.vertices.orEmpty()
        val points = mutableListOf<VSMMapPoint>()

        var vxIndex = 0
        for (tile in tiles) {
            val tileOrigin = TileUtils.decodeTileOrigin(tile.tileCode)
            while (vxIndex < vertices.size && vxIndex <= tile.lastVertexIndex) {
                val vertex = vertices[vxIndex]
                val skCoord = SKCoord.fromVertex(vertex.x, vertex.y, tileOrigin)
                val wgsPoint = CoordConverter.skToWGS84(skCoord)
                points.add(wgsPoint)
                Timber.d("Vertex ${vxIndex + 1} 변환 성공: 위도=${wgsPoint.latitude}, 경도=${wgsPoint.longitude}")
                vxIndex++
            }
        }
        Timber.d("총 ${points.size}개 좌표 변환 완료")
        points
    }

    private object TileUtils {
        private const val TILE_SIZE_X_DEGREE = 7.5 / 60.0
        private const val TILE_SIZE_Y_DEGREE = TILE_SIZE_X_DEGREE * 2.0 / 3.0

        fun decodeTileOrigin(tileCode: Int): TileOrigin {
            val xTileCode = tileCode and 0xFFFF
            val yTileCode = (tileCode shr 16) and 0xFFFF
            val mesh = xTileCode * 100 + yTileCode
            return TileOrigin(calcDminX(mesh), calcDminY(mesh))
        }

        private fun calcDminX(mesh: Int): Double {
            val a = mesh / 1000
            val c = (mesh % 100) / 10
            return 122.0 + a + (c - 1) * TILE_SIZE_X_DEGREE
        }

        private fun calcDminY(mesh: Int): Double {
            val a = mesh / 1000
            val b = (mesh - (a * 1000)) / 100
            val d = mesh % 10
            return (99.0 + (b - 1) * 2.0) / 3.0 + (d - 1) * TILE_SIZE_Y_DEGREE
        }
    }

    private object CoordConverter {
        fun skToWGS84(sk: SKCoord): VSMMapPoint {
            val lon = sk.x / SCALE
            val lat = sk.y / SCALE
            val (ecefX, ecefY, ecefZ) = geod2ecef(lat, lon)
            val (wgsX, wgsY, wgsZ) = bessel2wgs(ecefX, ecefY, ecefZ)
            val (wgsLat, wgsLon) = ecef2geod(wgsX, wgsY, wgsZ)
            return VSMMapPoint(wgsLon, wgsLat)
        }

        private fun geod2ecef(lat: Double, lon: Double): Triple<Double, Double, Double> {
            val latRad = Math.toRadians(lat)
            val lonRad = Math.toRadians(lon)
            val f = (BESSEL_A - BESSEL_B) / BESSEL_A
            val sqre = 2 * f - f * f
            val N = BESSEL_A / sqrt(1 - sqre * sin(latRad).pow(2))
            val x = N * cos(latRad) * cos(lonRad)
            val y = N * cos(latRad) * sin(lonRad)
            val z = N * (1 - sqre) * sin(latRad)
            return Triple(x, y, z)
        }

        private fun bessel2wgs(ecefX: Double, ecefY: Double, ecefZ: Double): Triple<Double, Double, Double> {
            val wgsX = ecefX + DX
            val wgsY = ecefY + DY
            val wgsZ = ecefZ + DZ
            return Triple(wgsX, wgsY, wgsZ)
        }

        private fun ecef2geod(x: Double, y: Double, z: Double): Pair<Double, Double> {
            val p = sqrt(x * x + y * y)
            val theta = atan((z * WGS84_A) / (p * WGS84_B))
            val sqrep = (WGS84_A * WGS84_A - WGS84_B * WGS84_B) / (WGS84_B * WGS84_B)
            val f = (WGS84_A - WGS84_B) / WGS84_A
            val sqre = 2 * f - f * f
            val latR = atan((z + sqrep * WGS84_B * sin(theta).pow(3)) /
                    (p - sqre * WGS84_A * cos(theta).pow(3)))
            val lonR = atan2(y, x)
            val lat = Math.toDegrees(latR)
            val lon = Math.toDegrees(lonR)
            return Pair(lat, lon)
        }
    }

    companion object {
        private const val BESSEL_A = 6377397.155
        private const val BESSEL_RF = 299.1528128
        private val BESSEL_B = BESSEL_A - BESSEL_A / BESSEL_RF

        private const val WGS84_A = 6378137.0
        private const val WGS84_RF = 298.257223563
        private val WGS84_B = WGS84_A - WGS84_A / WGS84_RF

        private const val DX = -147.0
        private const val DY = 506.0
        private const val DZ = 687.0

        private const val SCALE = 360000.0
    }
}