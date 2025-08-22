package com.example.mocklocation.data.datasource

import com.example.mocklocation.domain.entity.CurrentLocation
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    fun getCurrentLocation(): Flow<CurrentLocation>
    suspend fun hasLocationPermission(): Boolean
}