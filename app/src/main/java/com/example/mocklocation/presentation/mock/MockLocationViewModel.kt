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
) : ViewModel() {
    private val _isMockingStarted = MutableStateFlow(false)
    private val isMockingStarted: StateFlow<Boolean> = _isMockingStarted

    fun startMocking(gpsPoints: List<GpsPoint>) {
        if (gpsPoints.isEmpty()) return
        if (isMockingStarted.value) {
            mockLocationServiceLauncher.resumeMocking()
        } else {
            mockLocationServiceLauncher.startMocking(gpsPoints)
        }
        _isMockingStarted.value = true
    }

    fun stopMocking() {
        mockLocationServiceLauncher.stopMocking()
    }
}