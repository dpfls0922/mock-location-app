package com.example.mocklocation.presentation.mock

import com.example.mocklocation.di.ApplicationScope
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.SendMockLocationUseCase
import kotlinx.coroutines.*
import javax.inject.Inject

class MockLocationHandler @Inject constructor(
    private val sendMockLocationUseCase: SendMockLocationUseCase,
    @ApplicationScope private val scope: CoroutineScope
) {
    companion object {
        private const val MIN_DELAY_MS = 100L
    }
    private var mockJob: Job? = null

    fun startSending(gpsPoints: List<GpsPoint>) {
        if (gpsPoints.isEmpty()) return

        stopSending()

        mockJob = scope.launch {
            sendMockLocationUseCase.startMocking()
            gpsPoints.forEachIndexed { index, point ->
                sendMockLocationUseCase.sendLocation(point)
                delay(calculateDelay(gpsPoints, index))
            }
            sendMockLocationUseCase.stopMocking()
        }
    }

    private fun calculateDelay(gpsPoints: List<GpsPoint>, currentIndex: Int): Long {
        if (currentIndex >= gpsPoints.size - 1) return 0L // 마지막 지점은 딜레이 없음

        val currentTimestamp = gpsPoints[currentIndex].timestamp
        val nextTimestamp = gpsPoints[currentIndex + 1].timestamp
        val diff = nextTimestamp - currentTimestamp

        return if (diff > 0) diff else MIN_DELAY_MS
    }

    fun stopSending() {
        mockJob?.cancel()
        mockJob = null
    }
}