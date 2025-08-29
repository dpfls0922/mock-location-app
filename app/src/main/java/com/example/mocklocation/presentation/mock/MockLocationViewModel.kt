package com.example.mocklocation.presentation.mock

import androidx.lifecycle.ViewModel
import com.example.mocklocation.domain.entity.GpsPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MockLocationViewModel @Inject constructor(
    private val mockLocationServiceLauncher: MockLocationServiceLauncher,
    private val mockLocationHandler: MockLocationHandler
) : ViewModel() {
    val mockingState: StateFlow<MockingState> = mockLocationHandler.mockingState

    private val _gpsPoints = MutableStateFlow<List<GpsPoint>>(emptyList())
    val gpsPoints: StateFlow<List<GpsPoint>> = _gpsPoints.asStateFlow()

    fun startOrResumeMocking() {
        when (mockingState.value) {
            MockingState.IDLE -> {
                if (_gpsPoints.value.isNotEmpty()) {
                    mockLocationServiceLauncher.startMocking(_gpsPoints.value)
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

    fun updateGpsPoints(newPoints: List<GpsPoint>) {
        _gpsPoints.value = newPoints
    }
}