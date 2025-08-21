package com.example.mocklocation.data.service

import com.example.mocklocation.domain.service.MapService
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.VSMMapView
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VSMMapService @Inject constructor() : MapService {
    private var mapView: VSMMapView? = null
    private var mapEngine: MapEngine? = null

    override fun initializeMap(mapView: VSMMapView) {
        this.mapView = mapView
        this.mapEngine = mapView.mapEngine()
        Timber.d("Map initialized")
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