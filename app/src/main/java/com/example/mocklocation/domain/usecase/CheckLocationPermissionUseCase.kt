package com.example.mocklocation.domain.usecase

import com.example.mocklocation.domain.repository.LocationRepository
import javax.inject.Inject

class CheckLocationPermissionUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Boolean = locationRepository.checkLocationPermission()
}