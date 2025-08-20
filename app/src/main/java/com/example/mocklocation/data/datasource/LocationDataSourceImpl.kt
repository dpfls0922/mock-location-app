package com.example.mocklocation.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.example.mocklocation.di.DefaultDispatcher
import com.example.mocklocation.domain.entity.CurrentLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : LocationDataSource {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, LOCATION_REQUEST_INTERVAL_MS)
        .setMinUpdateIntervalMillis(LOCATION_REQUEST_FASTEST_INTERVAL_MS)
        .build()

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(): Flow<CurrentLocation> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(
                        CurrentLocation(
                            timestamp = loc.time,
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            accuracy = loc.accuracy,
                            bearing = loc.bearing
                        )
                    )
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }.flowOn(defaultDispatcher)

    companion object {
        private const val LOCATION_REQUEST_INTERVAL_MS = 3000L
        private const val LOCATION_REQUEST_FASTEST_INTERVAL_MS = 1000L
    }
}