package com.example.mocklocation.data.dto.request

import com.example.mocklocation.data.dto.common.RequestHeader
import kotlinx.serialization.Serializable

@Serializable
data class PoiSearchRequest(
    val header: RequestHeader,
    val searchTypCd: String,
    val reqCnt: Int = 20,
    val reqSeq: Int = 1,
    val name: String,
    val radius: String = "0",
    val poiGroupYn: String = "N",
    val noorX: String? = null,
    val noorY: String? = null,
    val areaName: String? = null,
    val reqSearchEngineInfo: ReqSearchEngineInfo? = null,
    val guideSearchType: String? = null,
    val realNoorX: String? = null,
    val realNoorY: String? = null,
    val searchLocationType: String? = null,
    val zoomLevel: String? = null,
    val geoPolygon: String? = null
)

@Serializable
data class ReqSearchEngineInfo(
    val searchFilterType: String = "2",
    val searchFrom: String = "main.sch"
)