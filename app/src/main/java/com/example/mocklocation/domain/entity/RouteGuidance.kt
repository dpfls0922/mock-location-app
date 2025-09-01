package com.example.mocklocation.domain.entity

data class RouteGuidance (
    val routeCount: Int,
    val destination: Destination?,
    val tvases: String?
)

data class Destination(
    val destXPos: Int,
    val destYPos: Int
)