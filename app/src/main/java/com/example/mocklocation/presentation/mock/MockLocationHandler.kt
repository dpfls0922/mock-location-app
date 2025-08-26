package com.example.mocklocation.presentation.mock

import com.example.mocklocation.di.ApplicationScope
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.SendMockLocationUseCase
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockLocationHandler @Inject constructor(
    private val sendMockLocationUseCase: SendMockLocationUseCase,
    @ApplicationScope private val scope: CoroutineScope
) {
    private var mockJob: Job? = null
    private var allGpsPoints: List<GpsPoint> = emptyList()
    private var lastSentIndex: Int = 0

    fun startSending(gpsPoints: List<GpsPoint>) {
        if (gpsPoints.isEmpty()) return
        stopSending()
        this.allGpsPoints = gpsPoints
        this.lastSentIndex = 0
        startMockingFromIndex()
    }

    fun resumeSending() {
        if (mockJob?.isActive == true || allGpsPoints.isEmpty()) return
        startMockingFromIndex()
    }

    private fun startMockingFromIndex() {
        if (lastSentIndex >= allGpsPoints.size) {
            resetIndex()
            return
        }

        mockJob = scope.launch {
            try {
                sendMockLocationUseCase.createMockLocationFlow(allGpsPoints, lastSentIndex)
                    .collect { point ->
                        sendMockLocationUseCase.sendLocation(point)
                        lastSentIndex++
                    }
            } finally {
                if (lastSentIndex >= allGpsPoints.size) {
                    resetIndex()
                }
            }
        }
    }

    private fun resetIndex() {
        this.lastSentIndex = 0
        this.allGpsPoints = emptyList()
    }

    fun stopSending() {
        mockJob?.cancel()
        mockJob = null
    }
}