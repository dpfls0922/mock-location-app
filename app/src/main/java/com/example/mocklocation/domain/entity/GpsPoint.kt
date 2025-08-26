package com.example.mocklocation.domain.entity

data class GpsPoint(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float,
    val satellites: Int
)