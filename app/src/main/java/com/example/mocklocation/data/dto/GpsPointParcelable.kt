package com.example.mocklocation.data.dto

import android.os.Parcelable
import com.example.mocklocation.domain.entity.GpsPoint
import com.skt.tmap.vsm.data.VSMMapPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class GpsPointParcelable(
    val provider: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val hAcc: Float,
    val vAcc: Float,
    val sAcc: Float,
    val speed: Float,
    val bearing: Float,
    val bAcc: Float,
    val satellites: Int,
    val maxCn0: Int,
    val meanCn0: Int
) : Parcelable

fun GpsPoint.toParcelable() = GpsPointParcelable(
    provider, timestamp, latitude, longitude, altitude,
    hAcc, vAcc, sAcc, speed, bearing, bAcc,
    satellites, maxCn0, meanCn0
)

fun GpsPointParcelable.toDomainModel() = GpsPoint(
    provider, timestamp, latitude, longitude, altitude,
    hAcc, vAcc, sAcc, speed, bearing, bAcc,
    satellites, maxCn0, meanCn0
)

fun VSMMapPoint.toGpsPoint(): GpsPoint {
    return GpsPoint(
        provider = "gps",
        timestamp = System.currentTimeMillis(),
        latitude = this.latitude,
        longitude = this.longitude,
        altitude = 5.0,
        hAcc = 5f,
        vAcc = 5f,
        sAcc = 5f,
        speed = 5f,
        bearing = 5f,
        bAcc = 5f,
        satellites = 3,
        maxCn0 = 3,
        meanCn0 = 3
    )
}