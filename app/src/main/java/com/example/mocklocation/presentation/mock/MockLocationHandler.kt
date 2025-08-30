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
    private var repeatMode: RepeatMode = RepeatMode.INFINITE
    private var repeatCount: Int = 0
    private var currentRepeat: Int = 0

    fun startSending(
        gpsPoints: List<GpsPoint>,
        repeatMode: RepeatMode,
        repeatCount: Int
    ) {
        if (gpsPoints.isEmpty()) {
            Timber.w("[WARN] GPS 지점이 없어 전송을 시작할 수 없습니다. (${mockingState.value})")
            return
        }
        stopSending()
        this.allGpsPoints = gpsPoints
        this.lastSentIndex = 0
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.currentRepeat = 0
        Timber.i("[MODE] 설정 적용: $repeatMode / count=$repeatCount / points=${gpsPoints.size}")
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

    fun updateGpsPoints(newPoints: List<GpsPoint>) {
        this.allGpsPoints = newPoints
        Timber.i("[UPDATE] GPS 포인트 업데이트: ${newPoints.size}개")
    }

    /**
     * 반복 설정만 업데이트 (실행 중이라면 다음 사이클부터 적용)
     */
    fun updateRepeatSettings(mode: RepeatMode, count: Int) {
        val oldMode = this.repeatMode
        val oldCount = this.repeatCount
        this.repeatMode = mode
        this.repeatCount = count

        if (oldMode != mode || oldCount != count) {
            currentRepeat = 0
            Timber.i("[MODE] 설정 업데이트: $mode / count=$count (현재상태: ${mockingState.value})")
        }
    }

    private fun startMockingFromIndex() {
        if (mockingState.value == MockingState.RUNNING) {
            Timber.w("[WARN] Mocking 작업이 이미 활성화되어 있습니다. (${mockingState.value})")
            return
        }

        mockJob = scope.launch {
            Timber.i("[START] Mocking 작업 시작 (${mockingState.value})")
            try {
                while (true) {
                    _mockingState.value = MockingState.RUNNING
                    if (repeatMode == RepeatMode.COUNT && currentRepeat >= repeatCount) {
                        Timber.i("[COMPLETE] 지정된 반복 횟수 완료: $currentRepeat/$repeatCount")
                        break
                    }
                    Timber.d("🔁 [${repeatMode.name} 모드] ${currentRepeat + 1} 회차 전송 시작")

                    sendMockLocationUseCase.createMockLocationFlow(
                        allGpsPoints,
                        lastSentIndex
                    ).collect { point ->
                        val result = sendMockLocationUseCase.sendLocation(point)
                        if (result.isFailure) {
                            Timber.e("[ERROR] 위치 전송 실패: ${point.latitude}, ${point.longitude}")
                        }
                        lastSentIndex++
                    }

                    currentRepeat++
                    lastSentIndex = 0
                    Timber.d("🔁 [${repeatMode.name} 모드] ${currentRepeat} 회차 전송 완료")
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
        this.currentRepeat = 0
        _mockingState.value = MockingState.IDLE
        Timber.i("[RESET] 상태 초기화 (${mockingState.value})")
    }
}