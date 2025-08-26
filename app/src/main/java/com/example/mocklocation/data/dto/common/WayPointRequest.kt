package com.example.mocklocation.data.dto.common

import kotlinx.serialization.Serializable

@Serializable
data class WayPointRequest(
    val x: Int,
    val y: Int,
    val poiID: String? = null,
    val rpFlag: Byte? = null
)