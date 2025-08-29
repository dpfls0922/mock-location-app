package com.example.mocklocation.presentation.mock

import com.example.mocklocation.di.ApplicationScope
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.SendMockLocationUseCase
import com.example.mocklocation.presentation.map.component.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

enum class MockingState {
    IDLE, RUNNING, PAUSED
}

@Singleton
class MockLocationHandler @Inject constructor(
    private val sendMockLocationUseCase: SendMockLocationUseCase,
    @ApplicationScope private val scope: CoroutineScope
) {
    private val _mockingState = MutableStateFlow(MockingState.IDLE)
    val mockingState: StateFlow<MockingState> = _mockingState.asStateFlow()

    private var mockJob: Job? = null
    private var allGpsPoints: List<GpsPoint> = emptyList()
    private var lastSentIndex: Int = 0

    fun startSending(
        gpsPoints: List<GpsPoint>,
        repeatMode: RepeatMode = RepeatMode.INFINITE,
        repeatCount: Int = 0,
    ) {
        if (gpsPoints.isEmpty()) {
            Timber.w("[WARN] GPS 지점이 없어 전송을 시작할 수 없습니다. (${mockingState.value})")
            return
        }
        stopSending()
        this.allGpsPoints = gpsPoints
        this.lastSentIndex = 0
        startMockingFromIndex()
    }

    fun resumeSending() {
        if (mockingState.value != MockingState.PAUSED) {
            Timber.w("[WARN] 재개할 수 있는 Mock 작업이 없습니다. (${mockingState.value})")
            return
        }
        startMockingFromIndex()
    }

    fun stopSending() {
        if (mockJob?.isActive == true) {
            mockJob?.cancel()
            mockJob = null
            _mockingState.value = MockingState.PAUSED
            Timber.i("[STOP] 작업 일시 정지 (${mockingState.value})")
        }
    }

    private fun startMockingFromIndex() {
        if (mockingState.value == MockingState.RUNNING) {
            Timber.w("[WARN] Mocking 작업이 이미 활성화되어 있습니다. (${mockingState.value})")
            return
        }

        mockJob = scope.launch {
            _mockingState.value = MockingState.RUNNING
            Timber.i("[START] Mocking 작업 시작 (${mockingState.value})")
            try {
                sendMockLocationUseCase.createMockLocationFlow(
                    allGpsPoints,
                    lastSentIndex,
                ).collect { point ->
                    val result = sendMockLocationUseCase.sendLocation(point)
                    if (result.isFailure) {
                        Timber.e("[ERROR] 위치 전송 실패: ${point.latitude}, ${point.longitude}")
                    }
                    lastSentIndex++
                }
            } finally {
                if (mockJob?.isCancelled == false) {
                    Timber.i("[FINISH] 작업 정상 완료")
                    resetState()
                }
            }
        }
    }

    private fun resetState() {
        this.lastSentIndex = 0
        this.allGpsPoints = emptyList()
        _mockingState.value = MockingState.IDLE
        Timber.i("[RESET] 상태 초기화 (${mockingState.value})")
    }
}