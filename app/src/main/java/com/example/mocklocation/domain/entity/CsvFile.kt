package com.example.mocklocation.domain.entity

import android.net.Uri

data class CsvFile(
    val id: String,
    val name: String,
    val uri: Uri,
    val uploadTime: Long = System.currentTimeMillis(),
    val gpsPoints: List<GpsPoint> = emptyList()
)