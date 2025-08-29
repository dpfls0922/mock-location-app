package com.example.mocklocation.presentation.mock

import androidx.lifecycle.ViewModel
import com.example.mocklocation.domain.entity.GpsPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MockLocationViewModel @Inject constructor(
    private val mockLocationServiceLauncher: MockLocationServiceLauncher,
    private val mockLocationHandler: MockLocationHandler
) : ViewModel() {
    val mockingState: StateFlow<MockingState> = mockLocationHandler.mockingState

    fun startOrResumeMocking(gpsPoints: List<GpsPoint>) {
        when (mockingState.value) {
            MockingState.IDLE -> {
                if (gpsPoints.isNotEmpty()) {
                    mockLocationServiceLauncher.startMocking(gpsPoints)
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
}