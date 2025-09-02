package com.example.mocklocation.data.service

import android.content.Context
import android.view.MotionEvent
import com.example.mocklocation.core.helper.MarkerHelper
import com.example.mocklocation.core.helper.MarkerType
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.service.MapService
import com.skt.tmap.vsm.data.VSMMapPoint
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.MapTouchEventHandler
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
    private var mapTouchListener: ((MotionEvent) -> VSMMapPoint?)? = null
    private var mapTouchEventHandler: MapTouchEventHandler? = null
    private var lastTouchedMarker: VSMMarkerBase? = null

    override fun createView(context: Context): VSMMapView {
        val view = VSMMapView(context)
        initializeMap(view)
        return view
    }

    private fun initializeMap(mapView: VSMMapView) {
        this.mapView = mapView
        this.mapEngine = mapView.mapEngine()
        markerHelper = MarkerHelper(mapView.context, mapView.mapEngine())
        mapEngine?.let {
            mapTouchEventHandler = MapTouchEventHandler(it)
            setupMapTouchEventHandler()
        }
        Timber.d("Map initialized")
    }

    override fun setMapTouchListener(listener: (MotionEvent) -> VSMMapPoint?) {
        this.mapTouchListener = listener
    }

    private fun setupMapTouchEventHandler() {
        val handler = mapView?.touchEventHandler()
        handler?.setOnGestureListener(object : MapTouchEventHandler.OnGestureListener {
            override fun onSingleTap(e: MotionEvent): Boolean {
                val mapPoint = mapTouchListener?.invoke(e)
                if (mapPoint != null) {
                    Timber.d("단일 탭 감지: 위도=${mapPoint.latitude}, 경도=${mapPoint.longitude}")
                }
                return true
            }

            override fun onLongPress(e: MotionEvent): Boolean {
                val mapPoint = mapTouchListener?.invoke(e)
                if (mapPoint != null) {
                    Timber.d("길게 누르기 감지: 위도=${mapPoint.latitude}, 경도=${mapPoint.longitude}")
                }
                return true
            }

            override fun onDown(e: MotionEvent): Boolean = false
            override fun onDoubleTap(e: MotionEvent): Boolean = false
            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = false
        })
    }

    override fun screenToWgs84(x: Int, y: Int): VSMMapPoint? {
        return mapEngine?.screenToWgs84(x, y)
    }

    override fun addMarkerAt(latitude: Double, longitude: Double) {
        removeExistingMarker()
        lastTouchedMarker = markerHelper?.addLocationMarker(
            latitude,
            longitude,
            MarkerType.TOUCH
        )
    }

    override fun removeExistingMarker() {
        lastTouchedMarker?.let { markerHelper?.removeMarker(it) }
        lastTouchedMarker = null
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

    override fun drawRouteLine(vsmPoints: List<VSMMapPoint>) {
        markerHelper?.drawPolyline(vsmPoints)
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