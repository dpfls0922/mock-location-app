package com.example.mocklocation.data.repository

import com.example.mocklocation.data.datasource.LocationDataSource
import com.example.mocklocation.di.IoDispatcher
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.repository.LocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {
    override fun getCurrentLocation(): Flow<CurrentLocation> {
        return locationDataSource.getCurrentLocation()
            .flowOn(ioDispatcher)
            .conflate()
    }

    override suspend fun checkLocationPermission(): Boolean =
        withContext(ioDispatcher) {
            locationDataSource.hasLocationPermission()
        }
}