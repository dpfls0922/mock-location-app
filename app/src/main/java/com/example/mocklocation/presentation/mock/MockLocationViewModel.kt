package com.example.mocklocation.presentation.mock

import androidx.lifecycle.ViewModel
import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.service.MapService
import com.example.mocklocation.presentation.map.component.GpsMode
import com.example.mocklocation.presentation.map.component.RepeatMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _repeatMode = MutableStateFlow(RepeatMode.INFINITE)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    private val _repeatCount = MutableStateFlow(3)
    val repeatCount: StateFlow<Int> = _repeatCount.asStateFlow()

    private val _speed = MutableStateFlow(60)
    val speed: StateFlow<Int> = _speed.asStateFlow()

    private val _gpsMode = MutableStateFlow(GpsMode.GPS)
    val gpsMode: StateFlow<GpsMode> = _gpsMode

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