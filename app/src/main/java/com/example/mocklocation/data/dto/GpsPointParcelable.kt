package com.example.mocklocation.data.dto

import android.os.Parcelable
import com.example.mocklocation.domain.entity.GpsPoint
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