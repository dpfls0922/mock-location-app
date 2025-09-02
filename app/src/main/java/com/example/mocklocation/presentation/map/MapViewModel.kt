package com.example.mocklocation.presentation.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mocklocation.data.service.MapServiceImpl
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.service.MapService
import com.example.mocklocation.domain.usecase.CheckLocationPermissionUseCase
import com.example.mocklocation.domain.usecase.GetCurrentLocationUseCase
import com.example.mocklocation.presentation.map.component.MockLocationUiModel
import com.skt.tmap.vsm.data.VSMMapPoint
import com.skt.tmap.vsm.map.VSMMapView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val checkPermissionUseCase: CheckLocationPermissionUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val mapService: MapService,
): ViewModel() {
    private val _locationPermissionGranted = MutableStateFlow(false)
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted

    private val _currentLocation = MutableStateFlow<CurrentLocation?>(null)
    val currentLocation: StateFlow<CurrentLocation?> = _currentLocation

    private val _currentLocationUiModel = MutableStateFlow<MockLocationUiModel?>(null)
    val currentLocationUiModel: StateFlow<MockLocationUiModel?> = _currentLocationUiModel

    private val _touchedLocation = MutableStateFlow<VSMMapPoint?>(null)
    val touchedLocation: StateFlow<VSMMapPoint?> = _touchedLocation.asStateFlow()

    fun createMapView(context: Context): VSMMapView {
        val mapView = mapService.createView(context)
        (mapService as MapServiceImpl).setMapTouchListener { event ->
            val mapPoint = mapService.screenToWgs84(event.x.toInt(), event.y.toInt())
            if (mapPoint != null && mapPoint.isValid) {
                _touchedLocation.value = mapPoint
            }
            mapPoint
        }
        return mapView
    }

    fun onTouchedLocationChanged(location: VSMMapPoint) {
        mapService.addMarkerAt(location.latitude, location.longitude)
    }

    suspend fun checkAndFetchInitialLocation() {
        val granted = checkPermissionUseCase()
        _locationPermissionGranted.value = granted
        if (granted) startLocationUpdates()
    }

    fun onPermissionResult(granted: Boolean) {
        _locationPermissionGranted.value = granted
        if (granted) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        getCurrentLocationUseCase()
            .onEach { location ->
                _currentLocation.value = location
                mapService.updateCurrentLocationMarker(location)
                Timber.d("위치 업데이트: $location")

                _currentLocationUiModel.value = MockLocationUiModel(
                    latitude = location.latitude,
                    longitude = location.longitude,
                )
            }
            .launchIn(viewModelScope)
    }

    fun drawRouteLine(vsmPoints: List<VSMMapPoint>) {
        mapService.drawRouteLine(vsmPoints)
    }

    fun onResume() = mapService.onResume()
    fun onPause() = mapService.onPause()
    fun cleanupMap() = mapService.cleanupMap()
}