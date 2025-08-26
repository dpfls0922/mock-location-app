package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.repository.MockLocationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SendMockLocationUseCase @Inject constructor(
    private val repository: MockLocationRepository
) {
    companion object {
        private const val MIN_DELAY_MS = 1L
    }

    fun createMockLocationFlow(gpsPoints: List<GpsPoint>, startIndex: Int) = flow {
        repository.startMocking()
        try {
            for (i in startIndex until gpsPoints.size) {
                val point = gpsPoints[i]
                emit(point)

                if (i < gpsPoints.size - 1) {
                    val nextPoint = gpsPoints[i + 1]
                    val diff = nextPoint.timestamp - point.timestamp
                    val delayTime = maxOf(diff, MIN_DELAY_MS)
                    delay(delayTime)
                }
            }
        } finally {
            repository.stopMocking()
        }
    }

    fun sendLocation(point: GpsPoint) {
        repository.sendLocation(point)
    }
}