package com.example.mocklocation.data.dto

import com.example.mocklocation.domain.entity.GpsPoint
import com.skt.tmap.vsm.data.VSMMapPoint

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