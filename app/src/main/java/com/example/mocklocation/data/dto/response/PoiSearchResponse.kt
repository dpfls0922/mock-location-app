package com.example.mocklocation.data.dto.response

import com.example.mocklocation.data.dto.common.BaseResponse
import com.example.mocklocation.data.dto.common.ResponseHeader
import com.example.mocklocation.domain.entity.Poi
import com.example.mocklocation.domain.entity.PoiFeature
import kotlinx.serialization.Serializable

@Serializable
data class PoiSearchResponse(
    override val header: ResponseHeader,
    val searchEngine: String,
    val totalCnt: Int,
    val listCnt: Int,
    val contFlag: String,
    val areaCnt: Int,
    val areaNm: String,
    val flatAreaCnt: Int,
    val flatAreaNm: String,
    val guideSearchYn: String,
    val guideSearchDisplayText: String,
    val guideSearchType: String,
    val globalYn: String,
    val aoiYn: String,
    val hotPlaceYn: String,
    val hotPlaceContainYn: String,
    val resSearchEngineInfo: String?,
    val poiSearches: List<PoiSearch>
) : BaseResponse

@Serializable
data class PoiSearch(
    val dbkind: String,
    val pkey: String,
    val poiId: String,
    val name: String,
    val orgName: String,
    val centerX: String,
    val centerY: String,
    val rpFlag: Byte,
    val parkYn: String,
    val phoneNum: String,
    val addr: String,
    val fullAddressJibun: String,
    val mlClass: String,
    val lcdName: String,
    val mcdName: String,
    val scdName: String,
    val dcdName: String,
    val fullAddressRoad: String,
    val classNmA: String,
    val classNmB: String,
    val classNmC: String,
    val classNmD: String,
    val feature: Feature?,
    val callCntTerm: String,
)

@Serializable
data class Feature(
    val parking: String?,
    val businessHours: List<BusinessHour>?,
    val images: List<Image>?
)

@Serializable
data class BusinessHour(
    val attrValue: String,
    val attrCd: String
)

@Serializable
data class Image(
    val cdnPath: String,
    val source: String?,
    val order: Int,
    val providerName: String?,
    val url: String
)

fun PoiSearch.toDomain(): Poi {
    val xPosInt = this.centerX.toIntOrNull() ?: 0
    val yPosInt = this.centerY.toIntOrNull() ?: 0

    return Poi(
        pkey = this.pkey,
        name = this.orgName,
        roadAddress = this.fullAddressRoad,
        rpFlag = this.rpFlag,
        xPos = xPosInt,
        yPos = yPosInt,
        distance = 0.0,
        feature = PoiFeature(
            hasParking = parkYn == "1",
            imgUrl = feature?.images?.firstOrNull()?.url
        )
    )
}