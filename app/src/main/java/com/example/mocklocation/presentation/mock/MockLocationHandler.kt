package com.example.mocklocation.presentation.mock

import com.example.mocklocation.di.ApplicationScope
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.usecase.MockLocationUpdate
import com.example.mocklocation.domain.usecase.NavigationState
import com.example.mocklocation.domain.usecase.SendMockLocationUseCase
import com.example.mocklocation.presentation.mock.component.GpsMode
import com.example.mocklocation.presentation.mock.component.RepeatMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
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

    private val _speedFlow = MutableStateFlow(60.0)
    val speedFlow: StateFlow<Double> = _speedFlow.asStateFlow()

    private val _gpsModeFlow = MutableStateFlow(GpsMode.GPS)
    val gpsModeFlow: StateFlow<GpsMode> = _gpsModeFlow.asStateFlow()

    private val _progressFlow = MutableStateFlow(0.0)
    val progressFlow: StateFlow<Double> = _progressFlow.asStateFlow()

    private var mockJob: Job? = null
    private var allGpsPoints: List<GpsPoint> = emptyList()

    private var lastSentIndex: Int = 0
    private var repeatMode: RepeatMode = RepeatMode.INFINITE
    private var repeatCount: Int = 0
    private var currentRepeat: Int = 0
    private var currentNavigationState: NavigationState? = null

    fun startSending(
        gpsPoints: List<GpsPoint>,
        repeatMode: RepeatMode,
        repeatCount: Int
    ) {
        if (gpsPoints.isEmpty()) {
            Timber.w("[WARN] GPS 지점이 없어 전송을 시작할 수 없습니다. (${mockingState.value})")
            return
        }
        reset()
        this.allGpsPoints = gpsPoints
        this.repeatMode = repeatMode
        this.repeatCount = repeatCount
        this.currentNavigationState = null
        Timber.i("[MODE] 설정 적용: $repeatMode / count=$repeatCount / points=${gpsPoints.size}")
        startMocking()
    }

    fun resumeSending() {
        if (mockingState.value != MockingState.PAUSED) {
            Timber.w("[WARN] 재개할 수 있는 Mock 작업이 없습니다. (${mockingState.value})")
            return
        }
        Timber.i("[RESUME] 마지막 인덱스: $lastSentIndex, currentNavigationState: $currentNavigationState")
        startMocking()
    }

    fun stopSending() {
        if (mockJob?.isActive == true) {
            mockJob?.cancel()
            mockJob = null
            _mockingState.value = MockingState.PAUSED
            Timber.i("[STOP] 작업 일시 정지 (${mockingState.value})")
        }
    }

    fun reset() {
        stopSending()
        resetState()
        this.allGpsPoints = emptyList()
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

    fun updateSpeed(speed: Int) {
        _speedFlow.value = speed.toDouble()
        Timber.i("[UPDATE] 전송 속도 업데이트: ${speed} km/h")
    }

    fun updateGpsMode(mode: GpsMode) {
        _gpsModeFlow.value = mode
        Timber.i("[UPDATE] GPS 모드 업데이트: ${mode} 모드")
    }

    private fun startMocking() {
        if (mockingState.value == MockingState.RUNNING) {
            Timber.w("[WARN] 이미 작업 중")
            return
        }

        mockJob = scope.launch {
            _mockingState.value = MockingState.RUNNING
            try {
                doRepeatLoop()
            } finally {
                if (mockJob?.isCompleted == true) {
                    Timber.i("[FINISH] 작업 정상 완료 (${mockingState.value})")
                    Timber.i("[FINISH] 반복 횟수 ($currentRepeat/$repeatCount)")
                    _progressFlow.value = 100.0
                    resetState()
                }
            }
        }
    }

    private suspend fun doRepeatLoop() {
        while (true) {
            if (repeatMode == RepeatMode.COUNT && currentRepeat >= repeatCount) break
            if (lastSentIndex >= allGpsPoints.size) {
                lastSentIndex = 0
                currentNavigationState = null
            }
            Timber.d("[🔁] ${repeatMode.name} 모드 ${currentRepeat + 1} 회차 시작")

            startCycle()

            currentRepeat++
            lastSentIndex = 0
            currentNavigationState = null
            Timber.d("[🔁] ${repeatMode.name} 모드 ${currentRepeat} 회차 완료")
        }
    }

    private suspend fun startCycle() {
        sendMockingFlow().collect { update ->
            when (update) {
                is MockLocationUpdate.Location -> handleLocationUpdate(update)
                is MockLocationUpdate.Progress -> {
                    _progressFlow.value = update.progressPercentage
                }
            }
        }
    }

    private fun sendMockingFlow() = flow {
        sendMockLocationUseCase.createMockLocationFlow(
            allGpsPoints,
            lastSentIndex,
            _speedFlow,
            gpsModeFlow,
            scope,
            currentNavigationState,
            onStateUpdate = { newState ->
                currentNavigationState = newState
                var newIndex = newState.nextPointIndex - 1
                if (newIndex < 0) newIndex = 0
                lastSentIndex = newIndex
            }
        ).collect {
            emit(it)
        }
    }

    private suspend fun handleLocationUpdate(update: MockLocationUpdate.Location) {
        val result = sendMockLocationUseCase.sendLocation(update.gpsPoint)
        if (result.isFailure) {
            Timber.e("[ERROR] 위치 전송 실패: ${update.gpsPoint.latitude},${update.gpsPoint.longitude}")
        }
    }

    private fun resetState() {
        this.lastSentIndex = 0
        this.currentRepeat = 0
        currentNavigationState = null
        _mockingState.value = MockingState.IDLE
        _progressFlow.value = 0.0
        Timber.i("[RESET] 상태 초기화 (${mockingState.value})")
    }
}