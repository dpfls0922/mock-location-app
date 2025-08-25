package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.GpsPoint
import com.example.mocklocation.domain.repository.MockLocationRepository
import javax.inject.Inject

class SendMockLocationUseCase @Inject constructor(
    private val repository: MockLocationRepository,
) {
    fun startMocking() {
        repository.startMocking()
    }

    fun sendLocation(point: GpsPoint) {
        repository.sendLocation(point)
    }

    fun stopMocking() {
        repository.stopMocking()
    }
}