package com.example.mocklocation.presentation.mock

import androidx.lifecycle.ViewModel
import com.example.mocklocation.domain.entity.GpsPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MockLocationViewModel @Inject constructor(
    private val mockLocationServiceLauncher: MockLocationServiceLauncher,
) : ViewModel() {
    fun startMocking(gpsPoints: List<GpsPoint>) {
        if (gpsPoints.isEmpty()) return
        mockLocationServiceLauncher.startMocking(gpsPoints)
    }

    fun stopMocking() {
        mockLocationServiceLauncher.stopMocking()
    }
}