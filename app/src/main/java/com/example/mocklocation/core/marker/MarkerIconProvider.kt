package com.example.mocklocation.core.marker

import android.content.Context
import android.graphics.Bitmap
import com.example.mocklocation.core.helper.MarkerType

interface MarkerIconProvider {
    fun getMarkerBitmap(context: Context, type: MarkerType): Bitmap?
}

// 마커 아이콘 팩토리
// START / END 타입: 도착 글씨를 추가한 비트맵 합성
// 나머지: 일반 아이콘
object DefaultMarkerIconProvider : MarkerIconProvider {
    override fun getMarkerBitmap(context: Context, type: MarkerType): Bitmap? {
        return when (type) {
            MarkerType.START -> { createEndpointMarker(context, type, "출발") }
            MarkerType.END -> { createEndpointMarker(context, type, "도착") }
            else -> { createDefaultMarker(context, type) }
        }
    }
}