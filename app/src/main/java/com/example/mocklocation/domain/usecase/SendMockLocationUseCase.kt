package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.repository.MockLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.cos
import kotlin.math.sqrt

class SendMockLocationUseCase @Inject constructor(
    private val repository: MockLocationRepository
) {
    companion object {
        private const val DEFAULT_UPDATE_INTERVAL_MS = 1000L
        private const val KMH_TO_MPS = 1000.0 / 3600.0
    }

    fun createMockLocationFlow(
        gpsPoints: List<GpsPoint>,
        startIndex: Int,
        speedFlow: Flow<Double>,
        scope: CoroutineScope
    ) = flow {
        repository.startMocking()
        Timber.d("🗺️ Mocking Flow 전송 시작: (${startIndex}/${gpsPoints.size})")

        try {
            var currentPoint = gpsPoints[startIndex]
            var nextIndex = startIndex + 1
            var accumulatedDistance = 0.0
            val totalDistance = gpsPoints.zipWithNext { a, b -> calculateDistance(a, b) }.sum()
            var emittedVertices = 0

            emit(currentPoint)
            emittedVertices++

            val speedState = MutableStateFlow(0.0)
            scope.launch(Dispatchers.Default) {
                speedFlow.collectLatest { speedKmh ->
                    speedState.value = speedKmh * KMH_TO_MPS
                }
            }

            while (accumulatedDistance < totalDistance) {
                val distancePerSecond = minOf(speedState.value, totalDistance - accumulatedDistance)

                val result = processDistance(
                    gpsPoints,
                    currentPoint,
                    nextIndex,
                    distancePerSecond,
                    accumulatedDistance,
                    emittedVertices,
                    speedState.value,
                    ::emit
                )

                currentPoint = result.first
                nextIndex = result.second
                emittedVertices = result.third
                accumulatedDistance += distancePerSecond

                delay(DEFAULT_UPDATE_INTERVAL_MS)
            }

            if (currentPoint != gpsPoints.last()) {
                emit(gpsPoints.last())
                emittedVertices++
            }

            Timber.i(
                "✅ Mocking 완료 | 총 버택스: $emittedVertices | " +
                        "총 주행거리: ${"%.2f".format(accumulatedDistance)} m | " +
                        "총 경로거리: ${"%.2f".format(totalDistance)} m"
            )
        } finally {
            repository.stopMocking()
        }
    }

    private suspend fun processDistance(
        gpsPoints: List<GpsPoint>,
        startPoint: GpsPoint,
        startIndex: Int,
        distanceToMove: Double,
        startAccumulated: Double,
        startVertices: Int,
        speed: Double,
        emit: suspend (GpsPoint) -> Unit
    ): Triple<GpsPoint, Int, Int> {
        var currentPoint = startPoint
        var nextIndex = startIndex
        var remainingDistance = distanceToMove
        var accumulatedDistance = startAccumulated
        var emittedVertices = startVertices

        while (remainingDistance > 0 && nextIndex < gpsPoints.size) {
            val nextPoint = gpsPoints[nextIndex]
            val distanceToNext = calculateDistance(currentPoint, nextPoint)

            if (distanceToNext >= remainingDistance) {
                val virtualPoint = interpolate(currentPoint, nextPoint, remainingDistance)
                emit(virtualPoint)

                val (newAccumulated, newVertices) = updateAfterEmit(
                    virtualPoint,
                    accumulatedDistance,
                    remainingDistance,
                    emittedVertices,
                    speed
                )

                currentPoint = virtualPoint
                accumulatedDistance = newAccumulated
                emittedVertices = newVertices
                remainingDistance = 0.0
            } else {
                val (newRemaining, newAccumulated) = moveToNextSegment(
                    nextPoint,
                    currentPoint,
                    remainingDistance,
                    accumulatedDistance
                )
                currentPoint = nextPoint
                remainingDistance = newRemaining
                accumulatedDistance = newAccumulated
                nextIndex++
            }
        }
        return Triple(currentPoint, nextIndex, emittedVertices)
    }

    private fun updateAfterEmit(
        virtualPoint: GpsPoint,
        accumulatedDistance: Double,
        remainingDistance: Double,
        emittedVertices: Int,
        speed: Double
    ): Pair<Double, Int> {
        val newAccumulated = accumulatedDistance + remainingDistance
        val newVertices = emittedVertices + 1

        Timber.v(
            "⏱ 1초 전송 | 이동거리: ${"%.2f".format(remainingDistance)} m | " +
                    "누적거리: ${"%.2f".format(newAccumulated)} m | " +
                    "위도: ${virtualPoint.latitude}, 경도: ${virtualPoint.longitude} | " +
                    "속도: ${"%.2f".format(speed)} m/s | " +
                    "버택스: $emittedVertices"
        )
        return newAccumulated to newVertices
    }

    private fun moveToNextSegment(
        nextPoint: GpsPoint,
        currentPoint: GpsPoint,
        remainingDistance: Double,
        accumulatedDistance: Double
    ): Pair<Double, Double> {
        val distanceToNext = calculateDistance(currentPoint, nextPoint)
        val newRemaining = remainingDistance - distanceToNext
        val newAccumulated = accumulatedDistance + distanceToNext

        Timber.v(
            "⏱ Segment 이동 | 이동거리: ${"%.2f".format(distanceToNext)} m | " +
                    "남은거리: ${"%.2f".format(newRemaining)} m | " +
                    "누적거리: ${"%.2f".format(newAccumulated)} m | " +
                    "위도: ${nextPoint.latitude}, 경도: ${nextPoint.longitude}"
        )
        return Pair(newRemaining, newAccumulated)
    }

    private fun interpolate(
        start: GpsPoint,
        end: GpsPoint,
        distanceToMove: Double
    ): GpsPoint {
        val segmentDistance = calculateDistance(start, end)
        if (segmentDistance == 0.0) return start
        val ratio = distanceToMove / segmentDistance

        return GpsPoint(
            provider = start.provider,
            timestamp = System.currentTimeMillis(),
            latitude = start.latitude + (end.latitude - start.latitude) * ratio,
            longitude = start.longitude + (end.longitude - start.longitude) * ratio,
            altitude = start.altitude + (end.altitude - start.altitude) * ratio,
            hAcc = start.hAcc,
            vAcc = start.vAcc,
            sAcc = start.sAcc,
            speed = start.speed,
            bearing = start.bearing,
            bAcc = start.bAcc,
            satellites = start.satellites,
            maxCn0 = start.maxCn0,
            meanCn0 = start.meanCn0
        )
    }

    private fun calculateDistance(p1: GpsPoint, p2: GpsPoint): Double {
        val dx = (p2.longitude - p1.longitude) * 111_000 * cos(Math.toRadians(p1.latitude))
        val dy = (p2.latitude - p1.latitude) * 111_000
        return sqrt(dx * dx + dy * dy)
    }

    fun sendLocation(point: GpsPoint): Result<Unit> {
        return repository.sendLocation(point)
    }
}