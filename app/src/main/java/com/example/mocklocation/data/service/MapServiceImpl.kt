package com.example.mocklocation.data.service

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.example.mocklocation.core.helper.MarkerHelper
import com.example.mocklocation.core.helper.MarkerType
import com.example.mocklocation.domain.entity.CurrentLocation
import com.example.mocklocation.domain.service.MapService
import com.example.mocklocation.presentation.route.component.RoutePoint
import com.skt.tmap.vsm.data.VSMMapPoint
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.MapTouchEventHandler
import com.skt.tmap.vsm.map.VSMMapView
import com.skt.tmap.vsm.map.marker.VSMMarkerBase
import com.skt.tmap.vsm.map.marker.VSMMarkerLocation
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapServiceImpl @Inject constructor() : MapService {
    private var mapView: VSMMapView? = null
    private var mapEngine: MapEngine? = null
    private var markerHelper: MarkerHelper? = null
    private var mapTouchListener: ((MotionEvent) -> VSMMapPoint?)? = null
    private var mapTouchEventHandler: MapTouchEventHandler? = null
    private var lastTouchedMarker: VSMMarkerBase? = null
    private var drawingPoints = mutableListOf<VSMMapPoint>()
    private var onDrawingPointListener: ((List<VSMMapPoint>) -> Unit)? = null
    private var drawingTouchListener: View.OnTouchListener? = null
    private var currentMarker: VSMMarkerLocation? = null
    private var isInitialLoad = false
    private var isTrackModeActive = false

    override fun createView(context: Context): VSMMapView {
        val view = VSMMapView(context)
        initializeMap(view)
        enableDrawingMode(false)
        return view
    }

    private fun initializeMap(mapView: VSMMapView) {
        this.mapView = mapView
        this.mapEngine = mapView.mapEngine()
        markerHelper = MarkerHelper(mapView.context, mapView.mapEngine())
        mapEngine?.let {
            mapTouchEventHandler = MapTouchEventHandler(it)
            setupMapTouchEventListener(mapView)
        }
        Timber.d("Map initialized")
    }

    override fun screenToWgs84(x: Int, y: Int): VSMMapPoint? {
        return mapEngine?.screenToWgs84(x, y)
    }

    override fun addMarkerAt(latitude: Double, longitude: Double) {
        removeLocationMarker()
        lastTouchedMarker = markerHelper?.addLocationMarker(
            latitude,
            longitude,
            MarkerType.TOUCH
        )
    }

    override fun removeLocationMarker() {
        lastTouchedMarker?.let { markerHelper?.removeMarker(it) }
        lastTouchedMarker = null
    }

    override fun removeDrawingMarker() {
        markerHelper?.removeAllMarkersExceptCurrent()
    }

    override fun updateCurrentLocationMarker(location: CurrentLocation) {
        if (currentMarker == null) {
            currentMarker = markerHelper?.addLocationMarker(
                location.latitude,
                location.longitude,
                MarkerType.CURRENT
            )
        } else {
            currentMarker?.apply {
                position = VSMMapPoint(location.longitude, location.latitude)
                bearing = location.bearing!!
            }
        }
        if (!isInitialLoad) {
            setCenter(location.latitude, location.longitude)
            isInitialLoad = true
        } else if (isTrackModeActive) {
            setCenter(location.latitude, location.longitude)
        }
    }

    override fun setTrackMode(enabled: Boolean) {
        isTrackModeActive = enabled
        if (isTrackModeActive) {
            currentMarker?.let {
                setCenter(it.position.latitude, it.position.longitude)
            }
        }
    }

    override fun setCenter(latitude: Double, longitude: Double) {
        markerHelper?.setCenter(latitude, longitude)
    }

    override fun drawRouteLine(vsmPoints: List<VSMMapPoint>, wayPoints: List<RoutePoint>?) {
        markerHelper?.drawPolyline(vsmPoints, wayPoints)
    }

    override fun clearDrawingPoints() {
        drawingPoints.clear()
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

    override fun setMapTouchListener(listener: (MotionEvent) -> VSMMapPoint?) {
        this.mapTouchListener = listener
    }

    override fun setDrawingPointListener(listener: (List<VSMMapPoint>) -> Unit) {
        this.onDrawingPointListener = listener
    }

    override fun enableDrawingMode(isEnable: Boolean) {
        mapView?.let { view ->
            if (isEnable) {
                /**
                 * 기존 터치 리스너 제거 후 경로 그리기 터치 리스너 설정
                */
                view.touchEventHandler()?.setOnGestureListener(null)
                setupDrawingTouchListener(view)
            } else {
                /**
                 * 경로 그리기 터치 리스너 제거 후 기존 터치 리스너 복구
                 */
                view.setOnTouchListener(null)
                setupMapTouchEventListener(view)
            }
        }
    }

    private fun setupDrawingTouchListener(mapView: VSMMapView) {
        mapView.setOnClickListener { }

        drawingTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    drawingPoints.clear()
                    removeDrawingMarker()
                    Timber.i("경로 그리기 시작")
                }
                MotionEvent.ACTION_MOVE -> {
                    val mapPoint = mapEngine?.screenToWgs84(event.x.toInt(), event.y.toInt())
                    if (mapPoint != null && mapPoint.isValid) {
                        drawingPoints.add(mapPoint)
                        markerHelper?.drawPolyline(drawingPoints)
                        Timber.i("경로 그리기 중: 위도=${mapPoint.latitude}, 경도=${mapPoint.longitude}")
                    }
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    onDrawingPointListener?.invoke(drawingPoints)
                    Timber.i("경로 그리기 종료: 총 ${drawingPoints.size}개 좌표 수집")
                }
            }
            true
        }
        mapView.setOnTouchListener(drawingTouchListener)
    }

    private fun setupMapTouchEventListener(mapView: VSMMapView) {
        mapView.touchEventHandler().setOnGestureListener(
            object : MapTouchEventHandler.OnGestureListener {
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
            }
        )
    }
}