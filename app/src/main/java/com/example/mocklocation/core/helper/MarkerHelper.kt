package com.example.mocklocation.core.helper

import android.content.Context
import com.example.mocklocation.R
import com.skt.tmap.vsm.data.VSMMapPoint
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.marker.VSMMarkerBase
import com.skt.tmap.vsm.map.marker.VSMMarkerLocation
import com.skt.tmap.vsm.map.marker.VSMMarkerPolyline
import com.example.mocklocation.core.marker.DefaultMarkerIconProvider
import com.example.mocklocation.core.marker.MarkerIconProvider
import com.example.mocklocation.presentation.route.component.RoutePoint
import com.skt.tmap.vsm.camera.CameraPosition
import com.skt.tmap.vsm.camera.CameraUpdate
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import com.skt.tmap.vsm.map.marker.MarkerImage
import timber.log.Timber

enum class MarkerType(val resId: Int, val iconSize: Pair<Float, Float>) {
    START(R.drawable.marker_start, Pair(40F, 40F)),
    END(R.drawable.marker_end, Pair(40F, 40F)),
    DESTINATION(R.drawable.ic_map_destinationpoint, Pair(35F, 35F)),
    WAYPOINT_1(R.drawable.ic_subtract_01, Pair(23F, 23F)),
    WAYPOINT_2(R.drawable.ic_subtract_02, Pair(23F, 23F)),
    CURRENT(R.drawable.marker_current, Pair(40F, 40F)),
    TOUCH(R.drawable.ic_subtract_destination, Pair(35F, 35F))
}

private val waypointMarkerTypes = mapOf(
    0 to MarkerType.WAYPOINT_1,
    1 to MarkerType.WAYPOINT_2
)

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)

class MarkerHelper(
    private val context: Context,
    private val mapEngine: MapEngine,
    private val iconProvider: MarkerIconProvider = DefaultMarkerIconProvider
) {
    private var currentMarker: VSMMarkerBase? = null

    fun addLocationMarker(
        latitude: Double,
        longitude: Double,
        type: MarkerType
    ): VSMMarkerLocation? {
        return runCatching {
            val position = VSMMapPoint(longitude, latitude)
            val iconInfo = iconProvider.getMarkerBitmap(context, type)
            val markerImage = iconInfo?.let { MarkerImage.fromBitmap(it) } ?: return@runCatching null
            val (width, height) = type.iconSize

            VSMMarkerLocation.Builder("marker_${type.name.lowercase()}")
                .position(position)
                .renderOrder(VSMMarkerBase.RENDERING_ORDER_AFTER_POINT_MARKER)
                .visible(true)
                .icon(markerImage)
                .iconSize(width, height)
                .showGuide(true)
                .renderMode(VSMMarkerLocation.RENDERMODE_BILLBOARD)
                .create().also { marker ->
                    mapEngine.markerManager.addMarker(marker)
                    if (type == MarkerType.CURRENT) {
                        currentMarker?.let { removeMarker(it) }
                        currentMarker = marker
                    }
                    Timber.d("마커 추가 성공: ${marker.id}")
                }
        }.onFailure { e ->
            Timber.e("위치 마커 추가 중 오류 발생: ${e.message}", e)
        }.getOrNull()
    }

    fun drawPolyline(
        points: List<VSMMapPoint>,
        wayPoints: List<RoutePoint>? = emptyList()
    ) {
        runCatching {
            removeAllMarkersExceptCurrent()

            if (points.size > 1) {
                addEdgeMarkers(points, wayPoints)

                val strokeColor = 0xFF2979FF.toInt()
                val fillColor = 0xAA2979FF.toInt()
                val polyline = VSMMarkerPolyline.Builder("PolylineMarker")
                    .points(points)
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .strokeWidth(4f)
                    .lineWidth(5f)
                    .create()
                mapEngine.markerManager.addMarker(polyline)
                moveCameraToPolyline(points)
                Timber.d("Polyline 정상적으로 지도에 표시됨")

            } else {
                Timber.e("경로 좌표가 비어있어 Polyline을 그릴 수 없습니다.")
            }
        }.onFailure { e ->
            Timber.e("Polyline 그리기 중 오류 발생", e)
        }
    }

    private fun addEdgeMarkers(
        points: List<VSMMapPoint>,
        wayPoints: List<RoutePoint>?
    ) {
        points.firstOrNull()?.let {
            addLocationMarker(it.latitude, it.longitude, MarkerType.START)
        }

        wayPoints?.forEachIndexed { i, wp ->
            val markerType = waypointMarkerTypes[i] ?: MarkerType.WAYPOINT_1
            val (skX, skY) = wp.location.xPos.toDouble() to wp.location.yPos.toDouble()
            val location = VSMCoordinates.skToWGS84(skX, skY)
            addLocationMarker(location[1], location[0], markerType)
        }

        points.lastOrNull()?.let {
            val offset = 0.00005
            addLocationMarker(it.latitude - offset, it.longitude, MarkerType.DESTINATION)
            addLocationMarker(it.latitude, it.longitude, MarkerType.END)
        }
    }

    private fun moveCameraToPolyline(points: List<VSMMapPoint>) {
        if (points.isEmpty()) return

        val box = calculateBoundingBox(points)
        val northEast = VSMMapPoint(box.maxLon, box.maxLat)
        val southWest = VSMMapPoint(box.minLon, box.minLat)
        val viewSize = mapEngine.surfaceSize
        val rect = android.graphics.Rect(0, 0, viewSize.x, viewSize.y)

        val cameraUpdate = object : CameraUpdate {
            override fun getCameraPosition(engine: MapEngine): CameraPosition? {
                val defaultPos = engine.getCameraPositionForBounds(northEast, southWest, rect) ?: return null
                val target = defaultPos.target ?: return null
                val zoomAdjusted = (defaultPos.zoom - 0.6).coerceAtLeast(engine.minZoomLevelLimit.toDouble())

                return CameraPosition(target, zoomAdjusted, defaultPos.tilt, defaultPos.bearing)
            }
        }

        val newPos = cameraUpdate.getCameraPosition(mapEngine)
        mapEngine.animateCamera(cameraUpdate)
        Timber.d("[카메라 이동 및 줌 완료] center: (${newPos?.target?.latitude}, ${newPos?.target?.longitude}), zoom: ${newPos?.zoom}")
    }

    private fun calculateBoundingBox(points: List<VSMMapPoint>): BoundingBox {
        var minLat = Double.MAX_VALUE
        var maxLat = Double.MIN_VALUE
        var minLon = Double.MAX_VALUE
        var maxLon = Double.MIN_VALUE

        for (point in points) {
            if (point.latitude < minLat) minLat = point.latitude
            if (point.latitude > maxLat) maxLat = point.latitude
            if (point.longitude < minLon) minLon = point.longitude
            if (point.longitude > maxLon) maxLon = point.longitude
        }
        return BoundingBox(minLat, maxLat, minLon, maxLon)
    }

    fun removeMarker(marker: VSMMarkerBase) {
        mapEngine.markerManager.removeMarker(marker)
        if (marker.id == currentMarker?.id) {
            currentMarker = null
        }
    }

    fun removeAllMarkersExceptCurrent() {
        mapEngine.markerManager.removeMarkerAll()
        currentMarker?.let {
            mapEngine.markerManager.addMarker(it)
        }
    }

    fun setCenter(latitude: Double, longitude: Double) {
        val result = mapEngine.setMapCenter(longitude, latitude, true)
        Timber.d("setCenter 호출 결과: $result")
    }

    fun zoomIn() {
        val result = mapEngine.setViewLevel(17, true)
        Timber.d("setCenter 호출 결과: $result")
    }
}