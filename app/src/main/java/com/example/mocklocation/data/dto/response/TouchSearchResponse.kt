package com.example.mocklocation.data.dto.response

import com.example.mocklocation.data.dto.common.BaseResponse
import com.example.mocklocation.data.dto.common.ResponseHeader
import com.example.mocklocation.domain.entity.TouchLocation
import kotlinx.serialization.Serializable

@Serializable
data class TouchSearchResponse(
    override val header: ResponseHeader,
    val listCnt: Int,
    val contFlag: Int,
    val poiSearches: List<TouchPoiSearch>
) : BaseResponse

data class TouchPoiSearch(
    val dbkind: String,
    val poiId: String,
    val name: String,
    val orgName: String,
    val addr: String
)

fun TouchSearchResponse.toDomain(): TouchLocation {
    val firstPoi = this.poiSearches.firstOrNull()
    return if (firstPoi != null) {
        TouchLocation(
            roadName = firstPoi.name,
            address = firstPoi.addr
        )
    } else {
        TouchLocation(
            roadName = "선택된 도로",
            address = "선택된 주소"
        )
    }
}