package com.example.mocklocation.data.dto.request

import com.example.mocklocation.data.dto.common.RequestHeader
import kotlinx.serialization.Serializable

@Serializable
data class TouchSearchRequest(
    val header: RequestHeader,
    val reqCnt: Int = 1,
    val reqSeq: Int = 1,
    val noorX: String,
    val noorY: String,
    val radius: String = "0",
    val addrDbYn: String = "Y",
    val poiDbYn: String = "Y"
)