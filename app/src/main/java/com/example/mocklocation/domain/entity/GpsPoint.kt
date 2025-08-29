package com.example.mocklocation.domain.entity

data class GpsPoint(
    val provider: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val hAcc: Float,      // horizontal accuracy
    val vAcc: Float,      // vertical accuracy
    val sAcc: Float,      // speed accuracy
    val speed: Float,
    val bearing: Float,
    val bAcc: Float,      // bearing accuracy
    val satellites: Int,
    val maxCn0: Int,
    val meanCn0: Int
)