package com.example.mocklocation.domain.repository

import com.example.mocklocation.domain.entity.CurrentLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<CurrentLocation>
}