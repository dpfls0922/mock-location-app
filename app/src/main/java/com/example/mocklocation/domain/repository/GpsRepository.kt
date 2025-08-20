package com.example.mocklocation.domain.repository

import com.example.mocklocation.domain.entity.GpsPoint
import java.io.File

interface GpsRepository {
    suspend fun getPointsFromCsv(file: File): List<GpsPoint>
}