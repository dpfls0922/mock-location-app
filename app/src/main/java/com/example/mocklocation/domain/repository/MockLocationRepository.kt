package com.example.mocklocation.domain.repository

import com.example.mocklocation.domain.entity.GpsPoint

interface MockLocationRepository {
    suspend fun sendLocation(point: GpsPoint): Result<Unit>
    suspend fun startMocking()
    suspend fun stopMocking()
}