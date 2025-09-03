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
import com.example.mocklocation.presentation.map.component.RoutePoint
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import com.skt.tmap.vsm.map.marker.MarkerImage
import timber.log.Timber

enum class MarkerType(val resId: Int, val iconSize: Pair<Float, Float>) {
    START(R.drawable.marker_start, Pair(30F, 30F)),
    END(R.drawable.marker_end, Pair(30F, 30F)),
    DESTINATION(R.drawable.ic_map_destinationpoint, Pair(30F, 30F)),
    WAYPOINT_1(R.drawable.ic_subtract_01, Pair(20F, 20F)),
    WAYPOINT_2(R.drawable.ic_subtract_02, Pair(20F, 20F)),
    CURRENT(R.drawable.marker_current, Pair(30F, 30F)),
    TOUCH(R.drawable.ic_subtract_destination, Pair(30F, 30F))
}

private val waypointMarkerTypes = mapOf(
    0 to MarkerType.WAYPOINT_1,
    1 to MarkerType.WAYPOINT_2
)

class MarkerHelper(
    private val context: Context,
    private val mapEngine: MapEngine,
    private val iconProvider: MarkerIconProvider = DefaultMarkerIconProvider
) {
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
                .bearing(0f)
                .iconSize(width, height)
                .showGuide(true)
                .renderMode(VSMMarkerLocation.RENDERMODE_BILLBOARD)
                .create().also { marker ->
                    mapEngine.markerManager.addMarker(marker)
                    Timber.d("마커 추가 성공: ${marker.id}")
                }
        }.onFailure { e ->
            Timber.e("위치 마커 추가 중 오류 발생: ${e.message}", e)
        }.getOrNull()
    }

    fun drawPolyline(
         points: List<VSMMapPoint>,
         wayPoints: List<RoutePoint> = emptyList()
    ) {
        runCatching {
            mapEngine.markerManager.removeMarkerAll()

            if (points.size > 1) {
                val startPoint = points.first()
                addLocationMarker(startPoint.latitude, startPoint.longitude, MarkerType.START)

                wayPoints.forEachIndexed { index, wayPoint ->
                    val markerType = waypointMarkerTypes.getOrElse(index) { MarkerType.WAYPOINT_1 }
                    val (skX, skY) = wayPoint.location.xPos.toDouble() to wayPoint.location.yPos.toDouble()
                    val location = VSMCoordinates.skToWGS84(skX, skY)
                    addLocationMarker(location[1], location[0], markerType)
                }

                val endPoint = points.last()
                addLocationMarker(endPoint.latitude, endPoint.longitude, MarkerType.DESTINATION)
                addLocationMarker(endPoint.latitude, endPoint.longitude, MarkerType.END)

                val pathColor = 0xFF42A5F5.toInt()
                val pathStrokeWidth = 4f
                val dashPattern = intArrayOf(4, 4, 12, 4)

                val polyline = VSMMarkerPolyline.Builder("PolylineMarker")
                    .points(points)
                    .strokeColor(pathColor)
                    .strokeWidth(pathStrokeWidth)
                    .lineDashStyle(dashPattern)
                    .create()
                mapEngine.markerManager.addMarker(polyline)
                Timber.d("Polyline 정상적으로 지도에 표시됨")

            } else {
                Timber.e("경로 좌표가 비어있어 Polyline을 그릴 수 없습니다.")
            }
        }.onFailure { e ->
            Timber.e("Polyline 그리기 중 오류 발생", e)
        }
    }

    fun removeMarker(marker: VSMMarkerBase) {
        mapEngine.markerManager.removeMarker(marker)
    }

    fun removeMarkerAll() {
        mapEngine.markerManager.removeMarkerAll()
    }

    fun setCenter(latitude: Double, longitude: Double) {
        val result = mapEngine.setMapCenter(longitude, latitude, true)
        Timber.d("setCenter 호출 결과: $result")
    }
}