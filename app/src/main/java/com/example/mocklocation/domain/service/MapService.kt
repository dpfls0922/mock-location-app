package com.example.mocklocation.domain.service

import com.skt.tmap.vsm.map.VSMMapView

interface MapService {
    fun initializeMap(mapView: VSMMapView)
    fun cleanupMap()
    fun onResume()
    fun onPause()
}