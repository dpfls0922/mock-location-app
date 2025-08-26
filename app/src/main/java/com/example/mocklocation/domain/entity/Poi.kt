package com.example.mocklocation.domain.entity

data class Poi(
    val pkey: String,
    val name: String,
    val roadAddress: String,
    val xPos: Int,
    val yPos: Int,
    val rpFlag: Byte,
    val distance: Double?,
    val feature: PoiFeature?
)

data class PoiFeature(
    val hasParking: Boolean,
    val imgUrl: String?
)