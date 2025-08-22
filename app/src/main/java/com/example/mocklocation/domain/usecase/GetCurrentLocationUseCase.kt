package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<CurrentLocation> =
        locationRepository.getCurrentLocation()
}