package com.example.mocklocation.domain.entity

data class RouteGuidance (
    val routeCount: Int,
    val destination: Destination?,
    val tvasData: ByteArray?
)

data class Destination(
    val destXPos: Int,
    val destYPos: Int
)