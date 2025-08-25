package com.example.mocklocation.domain.repository

import com.example.mocklocation.domain.entity.GpsPoint

interface MockLocationRepository {
    fun sendLocation(point: GpsPoint)
    fun startMocking()
    fun stopMocking()
}