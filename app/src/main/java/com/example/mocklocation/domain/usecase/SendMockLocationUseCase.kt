package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.repository.MockLocationRepository
import com.example.mocklocation.domain.service.GpsDistanceCalculator
import com.example.mocklocation.domain.service.GpsDistanceCalculator.Companion.FLOATING_POINT_TOLERANCE
import com.example.mocklocation.presentation.mock.component.GpsMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.atan2

sealed class MockLocationUpdate {
    data class Location(val gpsPoint: GpsPoint) : MockLocationUpdate()
    data class Progress(val progressPercentage: Double) : MockLocationUpdate()
}

data class NavigationState(
    val currentPoint: GpsPoint,
    val nextPointIndex: Int,
    val accumulatedDistance: Double,
    val emittedVertices: Int
)

class SendMockLocationUseCase @Inject constructor(
    private val repository: MockLocationRepository,
    private val distanceCalculator: GpsDistanceCalculator
) {
    companion object {
        private const val DEFAULT_UPDATE_INTERVAL_MS = 1000L
        private const val KMH_TO_MPS = 1000.0 / 3600.0
    }

    fun createMockLocationFlow(
        gpsPoints: List<GpsPoint>,
        startIndex: Int,
        speedFlow: Flow<Double>,
        gpsModeFlow: StateFlow<GpsMode>,
        scope: CoroutineScope,
        initialState: NavigationState? = null,
        onStateUpdate: ((NavigationState) -> Unit)? = null
    ): Flow<MockLocationUpdate> = flow {
        try {
            if (gpsPoints.isEmpty()) return@flow
            if (startIndex !in gpsPoints.indices) return@flow

            repository.startMocking()
            Timber.d("🗺️ Mocking Flow 전송 시작: (${startIndex}/${gpsPoints.size})")

            val totalPathDistance = distanceCalculator.calculateTotalDistance(gpsPoints)
            val speedState = MutableStateFlow(0.0)

            scope.launch(Dispatchers.Default) {
                speedFlow.collectLatest { speedKmh ->
                    speedState.value = speedKmh * KMH_TO_MPS
                }
            }

            var currentState = initialState ?: NavigationState(
                currentPoint = gpsPoints[startIndex],
                nextPointIndex = (startIndex + 1).coerceAtMost(gpsPoints.lastIndex),
                accumulatedDistance = 0.0,
                emittedVertices = 0
            )

            emit(MockLocationUpdate.Location(applyGpsMode(currentState.currentPoint, gpsModeFlow.value)))
            emit(MockLocationUpdate.Progress(0.0))
            currentState = currentState.copy(emittedVertices = currentState.emittedVertices + 1)

            while (!isNavigationComplete(currentState.accumulatedDistance, totalPathDistance)) {
                val speedMps = speedState.value
                val distanceToMove = minOf(speedMps, totalPathDistance - currentState.accumulatedDistance)

                currentState = moveAlongPath(
                    currentState,
                    gpsPoints,
                    distanceToMove,
                    speedMps,
                    gpsModeFlow.value,
                ) { gpsPoint ->
                    emit(MockLocationUpdate.Location(gpsPoint))
                }

                onStateUpdate?.invoke(currentState)

                val progress = (currentState.accumulatedDistance / totalPathDistance) * 100
                emit(MockLocationUpdate.Progress(progress))

                delay(DEFAULT_UPDATE_INTERVAL_MS)
            }

            if (currentState.currentPoint != gpsPoints.last()) {
                emit(MockLocationUpdate.Location(applyGpsMode(gpsPoints.last(), gpsModeFlow.value)))
                currentState = currentState.copy(emittedVertices = currentState.emittedVertices + 1)
            }
            emit(MockLocationUpdate.Progress(100.0))

            Timber.i(
                "✅ Mocking 완료 | 총 버택스: ${currentState.emittedVertices} | " +
                        "총 주행거리: ${"%.2f".format(currentState.accumulatedDistance)} m | " +
                        "총 경로거리: ${"%.2f".format(totalPathDistance)} m"
            )
        } finally {
            repository.stopMocking()
        }
    }

    private suspend fun moveAlongPath(
        currentState: NavigationState,
        gpsPoints: List<GpsPoint>,
        distanceToMove: Double,
        speed: Double,
        gpsMode: GpsMode,
        emitLocation: suspend (GpsPoint) -> Unit
    ): NavigationState {
        var mutableState = currentState
        var remainingDistance = distanceToMove

        while (remainingDistance > 0 && mutableState.nextPointIndex < gpsPoints.size) {
            val nextPoint = gpsPoints[mutableState.nextPointIndex]
            val distanceToNext = distanceCalculator.calculateDistance(mutableState.currentPoint, nextPoint)

            if (distanceToNext >= remainingDistance) {
                val virtualPoint = interpolate(mutableState.currentPoint, nextPoint, remainingDistance, distanceToNext)
                emitLocation(applyGpsMode(virtualPoint, gpsMode))
                logPositionUpdate(remainingDistance, mutableState.accumulatedDistance, speed, virtualPoint, mutableState.emittedVertices)
                mutableState = mutableState.copy(
                    currentPoint = virtualPoint,
                    accumulatedDistance = mutableState.accumulatedDistance + remainingDistance,
                    emittedVertices = mutableState.emittedVertices + 1
                )
                remainingDistance = 0.0
            } else {
                logSegmentMove(distanceToNext, remainingDistance, mutableState.accumulatedDistance, nextPoint)
                remainingDistance -= distanceToNext
                mutableState = mutableState.copy(
                    currentPoint = nextPoint,
                    accumulatedDistance = mutableState.accumulatedDistance + distanceToNext,
                    nextPointIndex = mutableState.nextPointIndex + 1
                )
            }
        }
        Timber.i("[moveAlongPath 루프 종료] 남은 거리: %.10f".format(remainingDistance))
        return mutableState
    }

    private fun isNavigationComplete(accumulatedDistance: Double, totalPathDistance: Double): Boolean {
        val remaining = totalPathDistance - accumulatedDistance
        return remaining <= FLOATING_POINT_TOLERANCE
    }

    private fun interpolate(
        start: GpsPoint,
        end: GpsPoint,
        distanceToMove: Double,
        segmentDistance: Double
    ): GpsPoint {
        if (segmentDistance <= FLOATING_POINT_TOLERANCE) return start
        val ratio = distanceToMove / segmentDistance
        val bearing = calculateBearing(start, end)

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
            bearing = bearing,
            bAcc = start.bAcc,
            satellites = start.satellites,
            maxCn0 = start.maxCn0,
            meanCn0 = start.meanCn0
        )
    }

    private fun applyGpsMode(point: GpsPoint, gpsMode: GpsMode): GpsPoint {
        val satellites = when (gpsMode) {
            GpsMode.GPS -> maxOf(point.satellites, 15)
            GpsMode.DR -> 0
            GpsMode.ORIGINAL -> point.satellites
        }
        return point.copy(satellites = satellites)
    }

    private fun calculateBearing(p1: GpsPoint, p2: GpsPoint): Float {
        val dx = (p2.longitude - p1.longitude)
        val dy = (p2.latitude - p1.latitude)
        return Math.toDegrees(atan2(dx, dy)).toFloat()
    }

    suspend fun sendLocation(point: GpsPoint): Result<Unit> {
        return repository.sendLocation(point)
    }

    private fun logPositionUpdate(movedDistance: Double, accumulatedDistance: Double, speed: Double, virtualPoint: GpsPoint, emittedVertices: Int) {
        Timber.v(
            "⏱ 위치 전송 | 이동: %.2f m | 누적: %.2f m | 속도: %.2f m/s | 위도: %.6f, 경도: %.6f | 버택스: %d"
                .format(movedDistance, accumulatedDistance + movedDistance, speed, virtualPoint.latitude, virtualPoint.longitude, emittedVertices)
        )
    }

    private fun logSegmentMove(movedDistance: Double, remainingDistance: Double, accumulatedDistance: Double, nextPoint: GpsPoint) {
        Timber.v(
            "⏱ 세그먼트 이동 | 이동: %.2f m | 남음: %.2f m | 누적: %.2f m | 위도: %.6f, 경도: %.6f "
                .format(movedDistance, remainingDistance - movedDistance, accumulatedDistance + movedDistance, nextPoint.latitude, nextPoint.longitude)
        )
    }
}