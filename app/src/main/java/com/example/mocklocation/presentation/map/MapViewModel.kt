package com.example.mocklocation.presentation.map

import androidx.lifecycle.ViewModel
import com.example.mocklocation.domain.service.MapService
import com.skt.tmap.vsm.map.VSMMapView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapService: MapService,
): ViewModel() {
    fun initializeMap(mapView: VSMMapView) = mapService.initializeMap(mapView)
    fun onResume() = mapService.onResume()
    fun onPause() = mapService.onPause()
    fun cleanupMap() = mapService.cleanupMap()
}