package com.example.mocklocation.presentation.mock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.service.MapService
import com.example.mocklocation.presentation.mock.component.GpsMode
import com.example.mocklocation.presentation.mock.component.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MockLocationViewModel @Inject constructor(
    private val mockLocationServiceLauncher: MockLocationServiceLauncher,
    private val mockLocationHandler: MockLocationHandler,
    private val mapService: MapService
) : ViewModel() {
    val mockingState: StateFlow<MockingState> = mockLocationHandler.mockingState

    private val _gpsPoints = MutableStateFlow<List<GpsPoint>>(emptyList())
    val gpsPoints: StateFlow<List<GpsPoint>> = _gpsPoints.asStateFlow()

    private val _progressState = MutableStateFlow(0)
    val progressState: StateFlow<Int> = _progressState.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.INFINITE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _repeatCount = MutableStateFlow(3)
    val repeatCount: StateFlow<Int> = _repeatCount.asStateFlow()

    private val _speed = MutableStateFlow(60)
    val speed: StateFlow<Int> = _speed.asStateFlow()

    private val _gpsMode = MutableStateFlow(GpsMode.GPS)
    val gpsMode: StateFlow<GpsMode> = _gpsMode

    private val _trackMode = MutableStateFlow(false)
    val trackMode: StateFlow<Boolean> = _trackMode

    init {
        mockLocationHandler.progressFlow
            .onEach { progress ->
                _progressState.value = progress.toInt()
            }
            .launchIn(viewModelScope)
    }

    fun startOrResumeMocking() {
        when (mockingState.value) {
            MockingState.IDLE -> {
                if (_gpsPoints.value.isNotEmpty()) {
                    mockLocationServiceLauncher.startMocking(
                        _gpsPoints.value,
                        _repeatMode.value,
                        _repeatCount.value
                    )
                }
            }
            MockingState.PAUSED -> {
                mockLocationServiceLauncher.resumeMocking()
            }
            MockingState.RUNNING -> {
            }
        }
    }

    fun stopMocking() {
        mockLocationServiceLauncher.stopMocking()
    }

    fun resetMocking() {
        mapService.removeDrawingMarker()
        mockLocationHandler.reset()
        resetGpsPoints()
    }

    private fun resetGpsPoints() {
        _gpsPoints.value = emptyList()
    }

    fun setTrackMode(enabled: Boolean) {
        _trackMode.value = enabled
        mapService.setTrackMode(enabled)
        if (mockingState.value == MockingState.IDLE) {
            mapService.zoomIn()
        }
    }

    fun updateGpsPoints(newPoints: List<GpsPoint>) {
        _gpsPoints.value = newPoints
        mockLocationHandler.updateGpsPoints(newPoints)
    }

    fun updateRepeatSettings(mode: RepeatMode, count: Int) {
        _repeatMode.value = mode
        _repeatCount.value = count
        mockLocationHandler.updateRepeatSettings(mode, count)
    }

    fun updateSpeed(newSpeed: Int) {
        _speed.value = newSpeed
        mockLocationHandler.updateSpeed(newSpeed)
    }

    fun updateGpsMode(mode: GpsMode) {
        _gpsMode.value = mode
        mockLocationHandler.updateGpsMode(mode)
    }
}