package com.example.mocklocation.domain.service

import android.content.Context
import android.view.MotionEvent
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.presentation.map.component.RoutePoint
import com.skt.tmap.vsm.data.VSMMapPoint
import com.skt.tmap.vsm.map.VSMMapView

/**
 * 외부 지도 SDK와 직접 연동해 지도 화면과 위치 표시 같은 플랫폼의 구체 기능을 처리하는 인프라스트럭처 서비스
 */
interface MapService {
    fun createView(context: Context): VSMMapView
    fun setMapTouchListener(listener: (MotionEvent) -> VSMMapPoint?)
    fun setDrawingPointListener(listener: (List<VSMMapPoint>) -> Unit)
    fun screenToWgs84(x: Int, y: Int): VSMMapPoint?
    fun addMarkerAt(latitude: Double, longitude: Double)
    fun removeLocationMarker()
    fun removeDrawingMarker()
    fun updateCurrentLocationMarker(location: CurrentLocation)
    fun setCenter(latitude: Double, longitude: Double)
    fun enableDrawingMode(isEnable: Boolean)
    fun drawRouteLine(vsmPoints: List<VSMMapPoint>, wayPoints: List<RoutePoint>)
    fun clearDrawingPoints()
    fun cleanupMap()
    fun onResume()
    fun onPause()
}