package com.example.mocklocation.data.service

import android.content.Context
import com.example.mocklocation.core.helper.MarkerHelper
import com.example.mocklocation.core.helper.MarkerType
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.service.MapService
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.VSMMapView
import com.skt.tmap.vsm.map.marker.VSMMarkerBase
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapServiceImpl @Inject constructor() : MapService {
    private var mapView: VSMMapView? = null
    private var mapEngine: MapEngine? = null
    private var markerHelper: MarkerHelper? = null
    private var currentMarker: VSMMarkerBase? = null

    override fun createView(context: Context): VSMMapView {
        val view = VSMMapView(context)
        initializeMap(view)
        return view
    }

    private fun initializeMap(mapView: VSMMapView) {
        this.mapView = mapView
        this.mapEngine = mapView.mapEngine()
        markerHelper = MarkerHelper(mapView.context, mapView.mapEngine())
        Timber.d("Map initialized")
    }

    override fun updateCurrentLocationMarker(location: CurrentLocation) {
        currentMarker?.let { markerHelper?.removeMarker(it) }
        currentMarker = markerHelper?.addLocationMarker(
            location.latitude,
            location.longitude,
            MarkerType.CURRENT
        )
        markerHelper?.setCenter(location.latitude, location.longitude)
    }

    override fun cleanupMap() {
        mapEngine?.onDestroy()
        val parent = mapView?.parent as? android.view.ViewGroup
        parent?.removeView(mapView)
        mapView = null
        mapEngine = null
        Timber.d("Map cleaned up")
    }

    override fun onResume() {
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
    }
}