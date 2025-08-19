package com.example.mocklocation.data.parser

import com.example.mocklocation.domain.entity.GpsPoint
import java.io.File

class CsvGpsParser(
    private val gpsRegex: Regex = DEFAULT_REGEX
) {
    fun parse(file: File): List<GpsPoint> {
        val result = mutableListOf<GpsPoint>()

        file.forEachLine { line ->
            if (!line.contains("#onLocationChanged")) return@forEachLine
            parseLine(line)?.let { result.add(it)}
        }
        return result
    }

    fun parseLine(line: String): GpsPoint? {
        val values = findValues(line, gpsRegex) ?: return null
        if (values.size != 6) return null

        val timestamp = values.getOrNull(0)?.toLongOrNull() ?: return null
        val latitude = values.getOrNull(1)?.toDoubleOrNull() ?: return null
        val longitude = values.getOrNull(2)?.toDoubleOrNull() ?: return null
        val accuracy = values.getOrNull(3)?.toFloatOrNull() ?: return null
        val speed = values.getOrNull(4)?.toFloatOrNull() ?: return null
        val bearing = values.getOrNull(5)?.toFloatOrNull() ?: return null

        return GpsPoint(timestamp, latitude, longitude, accuracy, speed, bearing)
    }

    fun findValues(line: String, regex: Regex): List<String>? {
        return regex.find(line)?.destructured?.toList()
    }

    companion object {
        val DEFAULT_REGEX = Regex(
            """time:\s*(\d+).*?Location\[gps\s*([\d.-]+),([\d.-]+).*?hAcc=([\d.]+).*?vel=([\d.]+).*?bear=([\d.]+)""",
            RegexOption.DOT_MATCHES_ALL
        )
    }
}
