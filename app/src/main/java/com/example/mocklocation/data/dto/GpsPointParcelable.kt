package com.example.mocklocation.data.dto

import android.os.Parcelable
import com.example.mocklocation.domain.entity.GpsPoint
import kotlinx.parcelize.Parcelize

@Parcelize
data class GpsPointParcelable(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float,
    val bearing: Float
) : Parcelable

fun GpsPoint.toParcelable() = GpsPointParcelable(
    timestamp, latitude, longitude, accuracy, speed, bearing
)

fun GpsPointParcelable.toDomainModel() = GpsPoint(
    timestamp, latitude, longitude, accuracy, speed, bearing
)