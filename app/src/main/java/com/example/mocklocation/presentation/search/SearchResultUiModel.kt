package com.example.mocklocation.presentation.search

import com.example.mocklocation.BuildConfig
import com.example.mocklocation.domain.entity.Poi
import com.skt.tmap.vsm.coordinates.VSMCoordinates
import com.skt.tmap.vsm.data.VSMMapPoint
import kotlin.Int

data class SearchResultUiModel(
    val pkey: String,
    val name: String,
    val roadAddress: String,
    val xPos: Int,
    val yPos: Int,
    val distanceText: String?,
    val hasParking: Boolean,
    val imgUrl: String?,
    val detailUrl: String
)

fun Poi.toUiModel(): SearchResultUiModel {
    val distanceText = distance?.let {
        if (it >= 1) String.format("%.1fkm", it)
        else "${(it * 1000).toInt()}m"
    }
    return SearchResultUiModel(
        pkey = pkey,
        name = name,
        roadAddress = roadAddress,
        xPos = xPos,
        yPos = yPos,
        distanceText = distanceText,
        hasParking = feature?.hasParking ?: false,
        imgUrl = feature?.imgUrl,
        detailUrl = "${BuildConfig.POI_DETAIL_URL}poi-detail?pkey=$pkey"
    )
}

fun VSMMapPoint.toUiModel(index: Int): SearchResultUiModel {
    val (xPos, yPos) = VSMCoordinates.wgs84ToSK(
        this.longitude,
        this.latitude
    )
    val nameWithIndex = "선택한 위치 ${index}"
    return SearchResultUiModel(
        pkey = "",
        name = nameWithIndex,
        roadAddress = "",
        xPos = xPos.toInt(),
        yPos = yPos.toInt(),
        distanceText = "",
        hasParking = false,
        imgUrl = "",
        detailUrl = ""
    )
}

fun SearchResultUiModel.toDomain(): Poi {
    return Poi(
        pkey = this.pkey,
        name = this.name,
        roadAddress = this.roadAddress,
        rpFlag = 0,
        xPos = this.xPos,
        yPos = this.yPos,
        distance = null,
        feature = null
    )
}