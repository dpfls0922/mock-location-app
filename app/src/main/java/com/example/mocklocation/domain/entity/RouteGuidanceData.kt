package com.example.mocklocation.domain.entity

import com.skt.tmap.vsm.data.VSMMapPoint

data class RouteGuidanceData(
    val tvasString: String,
    val gpsPoints: List<GpsPoint>,
    val vsmPoints: List<VSMMapPoint>
)