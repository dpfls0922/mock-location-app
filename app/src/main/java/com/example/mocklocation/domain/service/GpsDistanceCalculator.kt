package com.example.mocklocation.domain.service

import com.example.mocklocation.domain.entity.GpsPoint
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * GPS 지점 간의 거리 계산을 담당하는 클래스
 *
 * 200m 미만 거리는 빠른 유클리드 거리를 사용하고,
 * 200m 이상 거리는 정확한 하버사인 거리를 사용하여 성능과 정확도를 최적화함
 */
@Singleton
class GpsDistanceCalculator @Inject constructor() {
    companion object {
        private const val EARTH_RADIUS_METERS = 6_371_000.0
        private const val METERS_PER_DEGREE_LATITUDE = 111_321.0
        private const val DISTANCE_THRESHOLD = 200.0
        const val FLOATING_POINT_TOLERANCE = 0.04
    }

    fun calculateDistance(p1: GpsPoint, p2: GpsPoint): Double {
        val roughDistance = estimateDistance(p1, p2)
        return if (roughDistance < DISTANCE_THRESHOLD) {
            val distance = euclideanDistance(p1, p2)
            Timber.v("[유클리드]: %.3f m", distance)
            distance
        } else {
            val distance = haversineDistance(p1, p2)
            Timber.v("[하버사인]: %.3f m", distance)
            distance
        }
    }

    fun calculateTotalDistance(gpsPoints: List<GpsPoint>, startIndex: Int = 0): Double {
        if (gpsPoints.size <= 1 || startIndex >= gpsPoints.size - 1) {
            return 0.0
        }

        return (startIndex until gpsPoints.size - 1).sumOf { i ->
            calculateDistance(gpsPoints[i], gpsPoints[i + 1])
        }
    }

    /**
     * 두 지점의 위도 또는 경도 차이 중 더 큰 값을 기준으로 거리를 빠르게 추정함 (맨하튼 거리)
     * 이 값은 유클리드 거리와 하버사인 거리 중 어떤 공식을 사용할지 결정하는 데 사용됩니다.
     */
    private fun estimateDistance(p1: GpsPoint, p2: GpsPoint): Double {
        val latDiff = abs(p1.latitude - p2.latitude) * METERS_PER_DEGREE_LATITUDE
        val lonDiff = abs(p1.longitude - p2.longitude) * METERS_PER_DEGREE_LATITUDE
        return max(latDiff, lonDiff)
    }

    private fun euclideanDistance(p1: GpsPoint, p2: GpsPoint): Double {
        val avgLatitude = Math.toRadians((p1.latitude + p2.latitude) / 2.0)
        val metersPerDegreeLongitude = METERS_PER_DEGREE_LATITUDE * cos(avgLatitude)

        val dx = (p2.longitude - p1.longitude) * metersPerDegreeLongitude
        val dy = (p2.latitude - p1.latitude) * METERS_PER_DEGREE_LATITUDE
        return sqrt(dx * dx + dy * dy)
    }

    private fun haversineDistance(p1: GpsPoint, p2: GpsPoint): Double {
        val dLat = (p2.latitude - p1.latitude).toRadians()
        val dLon = (p2.longitude - p1.longitude).toRadians()

        val a = sin(dLat / 2).pow(2) + cos(p1.latitude.toRadians()) * cos(p2.latitude.toRadians()) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }
}

private fun Double.toRadians(): Double = Math.toRadians(this)